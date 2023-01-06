package surik.simyan.aliasika

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.flow.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.util.CoroutineTimer
import surik.simyan.aliasika.util.CoroutineTimerListener

class GameViewModel : ViewModel() {

    // Team one score
    private val _teamOneScore: CMutableStateFlow<Int> = MutableStateFlow(0).cMutableStateFlow()
    val teamOneScore: CStateFlow<Int> = _teamOneScore.cStateFlow()

    // Team two score
    private val _teamTwoScore: CMutableStateFlow<Int> = MutableStateFlow(0).cMutableStateFlow()
    val teamTwoScore: CStateFlow<Int> = _teamTwoScore.cStateFlow()

    // Time left
    private val _remainingTime: MutableStateFlow<String> = MutableStateFlow("")
    val remainingTime: CStateFlow<String> = _remainingTime.cStateFlow()

    // Words
    private var _words: CMutableStateFlow<List<String>> =
        MutableStateFlow((WORDS + PEOPLE).split(",")).cMutableStateFlow()
    var words: CStateFlow<List<String>> = _words.cStateFlow()

    // Five words
    private val _fiveWords: MutableStateFlow<List<String>> =
        MutableStateFlow<List<String>>(listOf()).cMutableStateFlow()
    val fiveWords: CStateFlow<List<String>> = _fiveWords.cStateFlow()

    private val _actions = Channel<Action>(Channel.BUFFERED)
    val actions: CFlow<Action> get() = _actions.receiveAsFlow().cFlow()

    // Gamemode
    var gamemode: Gamemode = Gamemode.STANDARD

    // Points to play
    var points = 100F

    // Time of one round
    var time = 45F

    // Team One Name
    var teamOneName = ""

    // Team Two Name
    var teamTwoName = ""

    // Winner team name
    var winnerTeam = ""

    // PlayingTeam
    var playingTeam = PlayingTeam.TeamOne

    // Guessed words count
    private var guessedWordsCount = 0

    fun wordGuessed(index: Int? = null) {
        addPoint(playingTeam)
        when (gamemode) {
            Gamemode.STANDARD -> {
                if (guessedWordsCount == 4) {
                    guessedWordsCount = 0
                    rotateWords()
                    getFiveWords()
                } else {
                    guessedWordsCount++
                }
            }
            Gamemode.SWIPE -> {
                rotateWords()
            }
            Gamemode.STACK -> {
                index?.let {
                    val word = words.value[index]
                    _words.value = (words.value.plus(word)).minus(words.value[index])
                }
            }
        }
    }

    fun wordUnguessed() {
        minusPoint(playingTeam)
        when (gamemode) {
            Gamemode.STANDARD -> {
                guessedWordsCount--
            }
            Gamemode.SWIPE -> {
                rotateWords(1)
            }
            else -> Unit
        }
    }

    private fun addPoint(playingTeam: PlayingTeam) {
        viewModelScope.launch {
            if (playingTeam == PlayingTeam.TeamOne) {
                _teamOneScore.emit(teamOneScore.value + 1)
            } else {
                _teamTwoScore.emit(teamTwoScore.value + 1)
            }
        }
    }

    private fun minusPoint(playingTeam: PlayingTeam) {
        viewModelScope.launch {
            if (playingTeam == PlayingTeam.TeamOne) {
                _teamOneScore.emit(teamOneScore.value - 1)
            } else {
                _teamTwoScore.emit(teamTwoScore.value - 1)
            }
        }
    }

    fun shuffleWords() {
        viewModelScope.launch {
            _words.value = words.value.shuffled()
            if (gamemode == Gamemode.STANDARD) {
                _fiveWords.emit(words.value.take(5))
            }
        }
    }

    fun startTimer() {
        Logger.d { "startTimer" }
        timer.startTimer(time.toLong())
    }

    fun pauseTimer() {
        timer.pauseTimer()
    }

    fun resumeTimer() {
        timer.continueTimer()
    }

    fun stopTimer() {
        timer.stopTimer()
    }

    private fun getFiveWords() {
        viewModelScope.launch {
            _fiveWords.emit(words.value.take(5))
            _actions.send(Action.FiveWordsGuessed).also {
                Logger.d { "FiveWordsGuessed" }
            }
        }
    }

    fun finishRoundEarly() {
        resumeTimer()
        stopTimer()
    }

    fun endGameEarly() {
        timer.destroyTimer()
        winnerTeam = if (teamOneScore.value > teamTwoScore.value) {
            teamOneName
        } else {
            teamTwoName
        }
        viewModelScope.launch {
            _actions.send(Action.GameEnded)
        }
    }

    // Timer
    private val timer = CoroutineTimer(object : CoroutineTimerListener {
        override fun onTick(timeLeft: Long?, error: Exception?) {
            _remainingTime.value = timeLeft.toString()
        }

        override fun onStop(error: Exception?) {
            super.onStop(error)
            timeFinished()
        }
    })

    private fun timeFinished() {
        roundEnded()
        viewModelScope.launch {
            _actions.send(Action.RoundFinished)
        }
    }

    private fun roundEnded() {
        playingTeam =
            if (playingTeam == PlayingTeam.TeamOne) PlayingTeam.TeamTwo else PlayingTeam.TeamOne
        _remainingTime.value = "0"

        if (playingTeam == PlayingTeam.TeamOne && teamOneScore.value >= points) {
            winnerTeam = teamOneName
            resetValues()
            viewModelScope.launch {
                _actions.send(Action.GameEnded)
            }
        } else if (playingTeam == PlayingTeam.TeamTwo && teamTwoScore.value >= points) {
            winnerTeam = teamTwoName
            resetValues()
            viewModelScope.launch {
                _actions.send(Action.GameEnded)
            }
        }
    }

    fun resetValues() {
        _teamOneScore.value = 0
        _teamTwoScore.value = 0
        gamemode = Gamemode.STANDARD
        points = 100F
        time = 45F
        teamOneName = ""
        teamTwoName = ""
        playingTeam = PlayingTeam.TeamOne
    }

    fun rotateWords(wordsAmount: Int? = null) {
        when (gamemode) {
            Gamemode.STANDARD -> {
                _words.value = (
                    words.value.drop(STANDARD_GAMEMODE_WORD_COUNT) + words.value.take(
                        STANDARD_GAMEMODE_WORD_COUNT
                    )
                    )
            }
            Gamemode.SWIPE -> {
                _words.value = (
                    words.value.drop(SWIPE_GAMEMODE_WORD_COUNT) + words.value.take(
                        SWIPE_GAMEMODE_WORD_COUNT
                    )
                    )
            }
            Gamemode.STACK -> {
                wordsAmount?.let {
                    _words.value = (words.value.drop(it) + words.value.take(it))
                }
            }
        }
    }

    sealed interface Action {
        object RoundFinished : Action
        object FiveWordsGuessed : Action
        object GameEnded : Action
    }

    enum class PlayingTeam {
        TeamOne, TeamTwo
    }

    enum class Gamemode {
        STANDARD, SWIPE, STACK
    }

    companion object {
        const val STANDARD_GAMEMODE_WORD_COUNT = 5
        const val SWIPE_GAMEMODE_WORD_COUNT = 1
        const val PEOPLE =
            "Richard Nixon, Harry Potter, Kobe Bryant, Jesus, Boris Yeltsin, Roberto Carlos, Matt Damon, Marty McFly, Tarzan, Van Helsing, Martin Scorsese, Neo, Julius Caesar, Paul McCartney, Bashar al-Assad, Ali Khamenei, Charles Dickens, Vincent Van Gogh, Mel Gibson, Woody Harrelson, Hello Kitty, Joe Biden, Stephen Hawking, Yanni, Claudius, Vlad the Impaler, Jake Gyllenhaal, Cesc Fàbregas, Bear Grylls, Bryan Adams, Snoop Dogg, Lana Del Rey, Tim Burton, Nicolas Cage, Julius Ceasar, Arnold Schwarzenegger, Hercules, Tom, Lady Gaga, Vladimir Putin, Joaquin Phoenix, David Beckham, Saddam Hussein, Irina Shayk, Frank Lampard, Ernest Hemingway, Ice Cube, Bonnie Parker, Hillary Clinton, Inna, Mike Wazowski, Batman, Rihanna, Channing Tatum, Fernando Torres, Angelina Jolie, V (V For Vendetta), Gandhi, Sandro Botticelli, Katy Perry, Ethan Hunt, Bob Marley, Clyde Barrow, Zac Efron, Jane Austen, Ace Ventura, George R. R. Martin, Hermione Granger, Steve Carell, Jim Carrey, Eric Cartman, Laurence Fishburne, Jim Parsons, Sting, Will Smith, Tony Soprano, Bob Dylan, Muhammad Ali, Adam Sandler, Tupac Shakur, Chris Evans, Nikola Tesla, Paul Allen, Gary Oldman, Milla Jovovich, 50 Cent, Frank Sinatra, Kevin Hart, Mike Tyson, Danny Trejo, Rasputin, Queen Elizabeth II, John Cena, Yuri Gagarin, Mark Hamill, Kafka, Adam Smith, Jim Morrison, Andrés Iniesta, Gianni Versace, Jennifer Lopez, Marlon Wayans, Nicole Kidman, Mickey Mouse, Johnny Depp, Aristotle, Michael Bloomberg, Robert Downey Jr., Debby Ryan, Luciano Pavarotti, Scrooge McDuck, Paul Walker, Pluto, Omar Khayyam, Julianne Moore, Dumbledore, King Kong, Prince Philip, Jackie Chan, Jaden Smith, Adam Savage, Mao Zedong, Liam Hemsworth, Tim Cook, Emmett Brown, Bruce Lee, Robert Pattinson, Donald Trump, Carles Puyol, Benito Mussolini, Galileo Galilei, Enrique Iglesias, Will Ferrell, Karl Marx, Post Malone, Christina Aguilera, Alexandra Daddario, Warren Buffett, Charles Darwin, will.i.am, Nicolaus Copernicus, Leonardo DiCaprio, Dory, Walter White, Lil Wayne, Richard Branson, Christian Bale, Andrea Pirlo, Dan Bilzerian, Bach, Wayne Rooney, Baymax, Billie Eilish, Deadpool, Pablo Escobar, Maluma, Skrillex, Luke Skywalker, Henry Ford, Fred Flintstone, Buddha, Alexander Graham Bell, Immanuel Kant, Macklemore, Jessie J, Beyoncé, Chopin, Cameron Diaz, Mark Twain, Chris Rock, Jimmy Fallon, Neil deGrasse Tyson, Harrison Ford, Severus Snape, Celine Dion, Marco Polo, Mikhail Gorbachev, John Travolta, Daniel Radcliffe, Gwen Stefani, Al Capone, Wolfgang Amadeus Mozart, Clark Gable, Richard Hammond, Zayn Malik, Andy Warhol, Novak Djokovic, Kesha, Lionel Messi, Rick Ross, Tony Hawk, Salma Hayek, Conor McGregor, Dracula, Elvis Presley, Marilyn Monroe, Rowan Atkinson, Michael J. Fox, Sean Paul, Xavi, Theodore Roosevelt, Dwayne Johnson, Meghan Trainor, Benjamin Franklin, Mark Zuckerberg, Clint Eastwood, Mario Götze, Frank Abagnale, Dana White, Mario, Selena Gomez, Coco Chanel, Mark Wahlberg, Sergey Brin, Freddy Krueger, Joker, Sheriff Woody, Usain Bolt, Sebastian Vettel, PSY, Freddie Highmore, Tiger Woods, XXXTentacion, Jay Z, Angela Merkel, Roger Federer, Casper, Marshmello, Hugh Hefner, Mario Gómez, Bill Clinton, Keanu Reeves, Larry Page, Nefertiti, Pink, Steven Seagal, Black Widow, Kylie Jenner, Britney Spears, Gandalf, William Shakespeare, David Copperfield, Isaac Newton, Lord Voldemort, Eminem, Gal Gadot, Bill Gates, Kermit the Frog, Peter Pan, Frankensteins Monster, Cardi B, Gordon Ramsay, Alfred Hitchcock, Martin Lawrence, Andrew Jackson, Gerard Piqué, Thomas Edison, Sandra Bullock, Genghis Khan, Christopher Lloyd, Donald Glover, Mark Webber, Barbie, Nicki Minaj, Jason Voorhees, Hellboy, Nicholas II, Christopher Nolan, Queen Elizabeth I, Woodrow Wilson, Cleopatra, Justin Trudeau, Captain America, Steve Jobs, Zorro, Samuel L. Jackson, Ronaldinho, Nemo, The Flash, Shakira, Wiz Khalifa, Rami Malek, Godzilla, Zlatan Ibrahimović, Radamel Falcao, R2-D2, Tyga, Shawn Mendes, John Locke, Cara Delevingne, David Luiz, Scarlett Johansson, Elizabeth Taylor, Stevie Wonder, Shrek, Natalie Portman, Donald Duck, Mark Ruffalo, Elton John, Leonid Brezhnev, Ray Charles, Donatello, Nelson Mandela, Roman Abramovich, Malcolm X, Optimus Prime, Kevin James, M.I.A., Buzz Lightyear, Mila Kunis, Usher, Pablo Picasso, Penelope Cruz, Martin Luther King, Frida Kahlo, Stephen King, Oprah Winfrey, Groot, Eazy-E, Barack Obama, Jared Leto, Tom Hardy, Shania Twain, Homer Simpson, Kim Jong-un, Shaquille ONeal, Bill Murray, Tiberius, Han Solo, Steven Spielberg, George Soros, Alex Ferguson, Dua Lipa, George Bush, Plato, Gianluigi Buffon, Raphael, Chris Hemsworth, Jay-Z, Ryan Reynolds, Zendaya, Taylor Swift, Ivan the Terrible, Kurt Cobain, Bruce Willis, Jeff Bezos, Uma Thurman, Ángel Di María, Serena Williams, Calvin Harris, Tyra Banks, Julia Roberts, Justin Bieber, Busta Rhymes, Joseph Stalin, Martin Freeman, Yoda, Diego Armando Maradona, Archimedes, Elsa, Ted, O. J. Simpson, Floyd Mayweather, Vito Corleone, Chris Brown, Michelangelo, Bambi, Jason Derulo, Charlie Chaplin, Kim Kardashian, Hugh Jackman, Robert Lewandowski, Thor, Edward Scissorhands, Future (rapper), Catwoman, Bradley Cooper, Wonder Woman, Demi Lovato, Eddie Murphy, Pitbull, Nero, Caligula, Quentin Tarantino, Buzz Aldrin, Winnie-the-Pooh, Michael Bay, Che Guevara, Kaley Cuoco, Elizabeth Olsen, Amy Winehouse, Jay Gatsby, Michel Teló, Justin Timberlake, Ariana Grande, Chip and Dale, Carl Sagan, John D. Rockefeller, Tom Hanks, Victor Hugo, Nikita Khrushchev, Ramses II, Emma Watson, Garry Kasparov, Jon Snow, George Washington, Christoph Waltz, Luis Fonsi, Hans Christian Andersen, Dalai Lama, Marie Antoinette, Marlon Brando, Alain Delon, The Pink Panther, Rafael Nadal, George Clooney, Pikachu, John F. Kennedy, Abraham Lincoln, Jensen Ackles, Christopher Columbus, Genie, Margot Robbie, Julio Iglesias, Cinderella, Mother Teresa, Liam Neeson, Captain Jack Sparrow, Leo Tolstoy, James Franco, Chloe Grace Moretz, Maximus, Mario Balotelli, Nicolas Sarkozy, Rocky Balboa, Goofy, Manuel Neuer, Jerry, Margaret Thatcher, Michael Jordan, Brad Pitt, Antonio Banderas, Robin Hood, Ryan Gosling, Robert De Niro, Emma Stone, Jason Statham, Arsène Wenger, Audrey Hepburn, Adolf Hitler, Jürgen Klopp, Wesley Snipes, Sarah Connor, LeBron James, John Legend, Maria Sharapova, Johan Cruyff, Jean-Jacques Rousseau, Akon, Louis Armstrong, Ed Sheeran, Miley Cyrus, The Grinch, Nick Jonas, Ben Affleck, Trajan, Pope Francis, Teenage Mutant Ninja Turtles, José Mourinho, Wall-E, James Bond, Elon Musk, J.K. Rowling, Goku, Alexander Pushkin, Elijah Wood, Wolverine, Agatha Christie, The Notorious B.I.G., Ellen Degeneres, Al Pacino, Socrates, David Guetta, Madonna, Willem Dafoe, Alfred Nobel, Kristen Stewart, Owen Wilson, Otto von Bismarck, Amelia Earhart, Michelle Rodriguez, Daniel Craig, Sonic the Hedgehog, Freddie Mercury, Forest Whitaker, Albert Enstein, Walt Disney, Beatrix Kiddo, David Bowie, Ne-Yo, Doctor Evil, Ludacris, Heath Ledger, Katniss Everdeen, Kate Winslet, Olaf, The Hulk, Tony Montana, Princess Diana, Billy Ray Cyrus, Keira Knightley, Edvard Munch, Leonardo Da Vinci, Chuck Berry, Augustus, Joan of Arc, Fidel Castro, Marilyn Manson, Rene Descartes, Lenin, Orlando Bloom, Napoleon Bonaparte, Ariel, Avril Lavigne, Alexander Hamilton, Serj Tankian, Paul Pogba, Johnny Galecki, Neymar, Santa Claus, George Orwell, Kanye West, Kevin Spacey, Prince Charles, Charlie Puth, Macaulay Culkin, Ronald Reagan, Tobey Maguire, Winston Churchill, Thomas Jefferson, Scooby-Doo, Sophocles, Lewis Hamilton, Osama Bin Laden, Travis Scott, John Lennon, Forrest Gump, Mahmoud Ahmadinejad, Peter Griffin, Neil Armstrong, Antonio Vivaldi, Stan Lee, Chuck Norris, Tom Cruise, Marie Curie, Megan Fox, Vasco Da Gama, Bugs Bunny, Pharrell Williams, J Balvin, Jony Ive, Andrea Bocelli, Jennifer Aniston, The Penguins of Madagascar, Hannibal Lecter, Obi-Wan Kenobi, Edgar Allan Poe, Cher, Ludwig van Beethoven, Confucius, Jack Black, MC Hammer, Minions, Whitney Houston, Barry White, Morgan Freeman, James Cameron, Vin Diesel, Kazimir Malevich, Michael Jackson, Leon Trotsky, Professor Moriarty, Zoey Deutch, Avicii, Tutankhamun, Dani Alves, SpongeBob SquarePants, Dr. Dre, The Weeknd, Pythagoras, Spiderman, Rembrandt, Etta James, Homer, Pele, Gabriel García Márquez, Gareth Bale, Jennifer Lawrence, Doctor Strange, Spock, Dumbo, Cruella De Vil, Sherlock Holmes, Ronald McDonald, Tony Stark (Iron Man), Larry King, Pac-Man, Darth Vader, Rita Ora, Pamela Anderson, Pocahontas, Cristiano Ronaldo, Muammar Gaddafi, Anne Hathaway, Sigmund Freud, Benedict Cumberbatch, Drake, Franklin D. Roosevelt, Popeye, Sylvester Stallone, Catherine OHara, Bruno Mars, Oscar Wilde, Indiana Jones, The Terminator, Superman, Queen Victoria, Camila Cabello, Adele, Claude Monet, Alexander III of Macedon, Salvador Dalí, Knaan, Naomi Campbell, Jeremy Clarkson, Giordano Bruno, Aladdin"
        const val WORDS =
            "affair, scientist, steam, economist, criminal, rumor, fill, solution, god, after, length, happiness, stretch, color, deadline, shade, alone, ministry, say, vacation, safe, economic, iron, format, military, faculty, mystery, margin, chip, else, ocean, eye, hook, stadium, occur, constitution, row, security, restriction, mass, bond, presence, wrist, farm, control, either, contract, complexity, variable, separation, wild, it, humanity, connection, adventure, serve, engineer, mathematics, toilet, gap, roof, oil, corps, require, graduate, leadership, spread, little, ability, frustration, organization, e-mail, criticism, reform, can, economy, board, celebration, manufacturer, peer, television, map, quarterback, lost, tournament, desert, identify, regulation, rail, department, fraction, bean, internet, into, out, movie, page, lose, make, system, bow, giant, teacher, song, might, glance, comparison, outcome, assumption, anxiety, movement, insight, provision, stop, nine, molecule, determine, addition, baker, variety, unit, dna, site, century, remain, root, difficulty, dream, towel, chin, income, magic, different, taste, residence, dish, arrest, mind, noon, disaster, objective, pound, write, fell, most, idea, weakness, trust, glove, testimony, delivery, counseling, republican, diversity, divorce, extent, average, flow, judge, processing, lemon, later, atmosphere, animal, please, sauce, victim, privilege, workshop, boss, sheet, starter, guilt, miracle, sign, effect, arm, garage, remove, instruction, cap, confusion, turn, method, hair, grace, approval, seek, prince, baby, lunch, police, production, try, dancer, jury, politics, exhibit, side, hurry, link, craft, billion, open, egg, signal, difference, identification, will, creation, pair, ad, tragedy, suburb, bridge, play, agent, tough, appear, personal, over, tradition, improve, treaty, any, bombing, what, humor, beauty, vessel, household, patch, budget, motivation, user, elevator, king, temperature, southern, coast, faith, guard, loan, human, guidance, radiation, phenomenon, limb, survivor, equity, license, software, snow, prosecutor, origin, risk, may, specialist, transition, white, quarter, pleasure, error, valley, studio, bull, ambassador, policy, everyone, dad, fighter, plain, bay, basis, function, planet, section, fate, forget, transportation, measurement, and, respond, seven, radio, opposition, forest, shift, went, wolf, photograph, pain, beautiful, colony, chief, plastic, minute, principal, gentle, exist, reflection, been, satisfaction, hall, sleep, advocate, push, better, cigarette, city, material, pollution, raise, bias, boot, international, next, head, air, war, exception, rat, confidence, engagement, inspector, housing, art, increase, buy, silence, artist, juice, spend, study, across, script, neighborhood, remember, pump, gene, landing, cut, travel, necessary, receive, boat, therapist, quotient, weight, membership, performance, instructor, west, cancer, veteran, last, motor, lip, tank, midnight, exact, about, task, uncertainty, fight, why, winner, process, lens, portion, skill, ethics, coal, noise, since, resource, suggestion, throat, servant, practice, key, surgery, candidate, night, spell, before, final, cheese, us, really, shake, sharp, nightmare, experiment, cliff, low, loud, importance, machine, demonstration, strike, pretty, hearing, kept, piano, legislation, enter, march, excuse, reed, concept, storage, area, shot, jacket, cause, narrative, reduce, necessity, globe, duty, along, lab, run, course, rice, stood, gallery, producer, horse, rep, palace, three, crime, all, district, east, gear, porch, passenger, discuss, professor, recovery, breath, wash, culture, expect, crash, earth, duck, church, million, shoulder, object, explain, weather, influence, daddy, morning, keep, traveler, chance, service, debt, establishment, plant, chemical, appearance, meter, picture, boy, limit, stair, old, professional, cycle, hunt, counselor, person, commitment, architect, including, around, railroad, scholarship, energy, audience, author, dark, cell, load, era, obligation, marriage, likely, pit, fun, application, deal, screen, eat, threat, hospital, brought, fig, potato, turkey, find, democratic, management, media, produce, wrong, passage, president, shelter, village, category, assignment, commerce, sheriff, publisher, step, party, depression, climate, was, notion, spending, table, children, once, strip, name, ankle, helicopter, debate, extension, felt, volume, emotion, slope, pole, guess, historian, crease, prevention, attack, young, fence, clearly, quiet, measure, instant, market, collection, stand, tend, profit, elbow, flavor, situation, cloth, heel, shine, smile, general, discrimination, bad, love, work, specific, mess, quality, enthusiasm, matter, corn, involvement, mud, jeans, thing, metaphor, drop, insurance, computer, viewer, spot, peace, month, religious, meaning, feed, illness, cash, country, lock, advertising, host, kit, american, never, olympics, cup, red, realize, behind, beach, blood, star, physician, lane, call, appeal, popular, far, transaction, store, basket, chain, angle, reputation, return, opinion, opera, welfare, wheel, drive, stimulus, database, significant, chronicle, killing, classic, transfer, care, perception, eagle, throw, fair, stuff, sky, diagnosis, mix, subject, cholesterol, stock, double, home, carrier, sidebar, chocolate, infant, guitar, found, opportunity, stomach, learning, twenty, together, lawyer, criteria, huge, check, connect, communication, door, scene, become, both, correlation, division, sink, promise, expert, customer, tire, creature, syllable, fire, championship, engineering, engine, expectation, space, festival, lady, important, branch, tribe, beer, kind, great, frequency, chicken, swim, people, safety, card, tonight, analyst, telephone, recommendation, sir, array, discussion, green, action, sequence, breakfast, suggest, settlement, gentleman, daughter, navy, trip, bowl, drinking, consequence, terrorism, worker, scope, evil, novel, representative, photo, medicine, caught, tactic, transmission, up, differ, anything, hypothesis, poor, justice, one, mechanism, satellite, student, main, saving, help, smell, booth, manner, failure, assault, capital, feeling, civilian, medical, gang, yellow, diabetes, committee, told, emission, man, pocket, follow, list, donor, expression, questionnaire, web, ford, barrier, county, funding, broke, give, fort, age, inflation, signature, magnet, generation, hang, success, world, choice, guide, businessman, inquiry, fear, virus, hill, school, cost, teach, photography, alcohol, brain, result, skirt, see, researcher, too, range, project, spokesman, envelope, large, spirit, die, crop, grandmother, hope, question, region, gas, ingredient, warrior, more, competitor, frame, nail, pro, sculpture, draft, troop, only, eating, laboratory, ride, education, correspondent, reveal, reporting, living, down, diamond, language, ghost, penalty, trouble, stress, revolution, well, anyone, each, whose, yard, fashion, leg, heaven, childhood, therapy, injury, employment, commissioner, proper, group, men, option, bottom, commission, compromise, toy, girl, surprise, property, command, queen, rod, vowel, environment, traditional, floor, profession, yeah, enough, scholar, curve, face, road, hotel, journal, aide, court, several, society, heart, have, truck, blog, detective, poet, intensity, parade, video, desk, coat, fly, shelf, presidency, interest, ear, shame, jay, wound, relationship, philosophy, speak, even, symbol, personnel, resistance, mark, add, integration, tube, reality, drink, being, academy, disease, repeat, mouse, high, darkness, manage, black, winter, pie, chef, short, stick, doctrine, response, blue, critic, realm, experience, look, provider, ring, tell, astronomer, herb, grand, corruption, upon, win, fluid, retirement, coverage, continue, oven, representation, intention, employee, candy, doubt, game, order, farmer, arena, content, that, terror, local, those, above, mayor, appointment, insect, poem, book, public, intelligence, dust, pipe, colleague, network, boom, listen, remark, throughout, instead, community, privacy, suspect, birth, point, noun, edition, buddy, cry, garden, whether, fellow, sister, consonant, tumor, hat, rock, apple, bible, finish, instance, technique, wire, answer, news, gold, bunch, grass, agenda, pearl, everybody, offer, prize, border, right, seem, works, kingdom, element, clinic, manager, developer, wall, proof, through, owner, warning, journey, governor, opposite, summer, mail, champion, gone, sample, written, town, depth, round, bit, explosion, jail, flight, sight, status, tone, parent, carbon, paragraph, move, present, we, comedy, forward, analysis, amount, volunteer, financial, island, street, develop, immigrant, copy, create, illustration, bar, fox, composition, crack, aircraft, staff, shop, same, brother, nation, curtain, trial, invasion, stability, team, wife, topic, thus, part, dog, fiction, conclusion, bike, case, asset, pig, ever, trail, sanction, club, father, select, recognition, peak, health-care, journalist, finding, heard, busy, imagination, happy, butter, natural, soldier, bone, physical, verb, political, future, suicide, share, ally, lawn, reaction, take, string, special, capitol, let, venture, cream, certain, reply, join, especially, neighbor, rise, advantage, coffee, mood, sidewalk, equal, honor, reduction, wine, scale, drama, cattle, duke, avoid, excite, reporter, aspect, tie, actually, rain, dollar, grandfather, other, combination, cooking, cop, storm, clean, suspicion, moon, grew, contain, direction, my, belly, bright, shirt, gravity, tablespoon, firm, proportion, tech, drawing, ease, beam, pitcher, know, rope, activity, protection, actress, consumption, maybe, shuttle, administrator, sun, subtract, beyond, from, discovery, single, tip, acceptance, container, bell, decimal, major, foreign, heat, doll, flame, export, writing, flour, palm, current, clue, permission, melody, involve, priority, husband, tooth, among, agriculture, hear, had, platform, prescription, newspaper, lone, incentive, investment, conduct, cotton, approach, barrel, apartment, learn, teeth, entry, race, limitation, bag, child, writer, rush, indicator, life, lay, grocery, money, stream, investigator, afternoon, deer, dispute, mixture, thick, session, tent, context, hour, magnitude, commercial, save, crown, observer, parking, recipe, research, particle, punishment, layer, power, straight, hurricane, transformation, serving, image, knee, fantasy, affect, substance, south, sale, allegation, ready, surgeon, tiny, count, glass, promotion, agreement, sweat, way, league, bread, pillow, exactly, accident, explanation, neck, others, argument, phrase, structure, example, terrorist, crowd, chart, theory, math, deck, dynamics, wedding, plate, law, jersey, photographer, hot, indeed, lawsuit, incident, glad, grain, pack, record, than, reception, conviction, hunting, nut, station, kid, monster, resolution, body, assistant, phase, argue, caller, encounter, maintenance, chick, every, cross, worry, guest, mask, belief, expertise, shoe, tissue, conservative, search, football, controversy, hostage, ray, path, national, word, canvas, trade, test, senator, chest, feet, son, estimate, contractor, phone, bureau, evening, very, block, plural, social, existence, immigration, stead, vitamin, twin, panel, best, salt, kill, defense, send, fruit, code, classroom, product, somebody, usual, first, self, consider, feedback, currency, psychology, reach, visitor, leaf, except, pressure, here, chapter, dinner, plot, senate, were, access, victory, mortgage, institute, apply, motion, handle, understanding, term, feature, norm, bring, concentration, focus, brand, landscape, heritage, brown, meat, strain, harm, athlete, charge, size, laugh, positive, box, imagine, silk, anger, accuracy, enterprise, efficiency, relative, ago, habit, attorney, purpose, account, medication, left, personality, silver, cruise, album, wisdom, muscle, tour, gray, labor, growth, official, tourist, serious, certainly, dining, locate, bomb, soon, trait, act, dirt, dead, stage, diet, female, sandwich, whale, correct, trend, horror, musician, lesson, none, channel, report, poster, successful, everything, opponent, yes, gain, powder, regime, grow, violation, rifle, between, ice, distinction, ticket, sense, religion, schedule, simply, common, feel, room, telescope, parliament, intervention, elite, counter, legend, hallway, council, gesture, saint, honey, testing, factory, contest, foot, unity, contribution, two, traffic, pose, prayer, pop, pension, talent, rhythm, lead, founder, performer, federal, walker, much, bottle, destruction, survival, port, populate, proposal, literature, their, leader, institution, unemployment, paper, driver, treat, rather, today, vote, operator, furniture, protein, hit, believe, teaching, mother, gift, missile, emphasis, tool, broadcast, opening, exploration, depend, visit, worth, yet, possibility, separate, off, ruling, designer, bedroom, improvement, log, symptom, hundred, employer, reflect, particularly, press, clear, get, sales, fail, occupation, meant, eight, took, election, theme, cabin, fault, physics, joke, consideration, decide, cat, just, admit, then, museum, expansion, tale, skin, knowledge, prisoner, stem, forum, teen, tobacco, land, stroke, participation, mode, cool, forehead, nice, routine, set, mill, fat, luck, band, airport, finally, kitchen, form, pace, comment, women, ratio, fighting, activist, leather, aim, though, you, arrival, decade, carry, stake, respect, passion, death, conversation, prison, until, pill, brick, sunlight, maintain, few, lover, tunnel, hole, problem, mile, fast, gate, flower, ski, roll, progress, steel, assistance, pilot, announcement, solve, company, tea, couch, standard, tax, pond, ok, disk, thanks, capacity, note, moment, probable, consumer, clay, onion, inside, supporter, wish, height, milk, formula, whole, commander, attention, shadow, advice, essay, interior, tower, timing, version, original, fishing, announcer, canyon, holiday, pine, occasion, recipient, competition, painting, like, blade, repair, supervisor, spouse, when, interpretation, chamber, suit, prove, something, are, pattern, possession, there, manufacturing, held, standing, funeral, rebel, investigation, dialogue, reader, wood, toe, spectrum, desire, destination, lifetime, purchase, tendency, building, soil, lifestyle, basic, lack, sentence, environmental, character, clothes, affiliation, interview, treatment, back, associate, avenue, arrive, multiply, someone, pregnancy, ranch, need, job, excitement, plan, horoscope, hip, sport, species, airline, blanket, represent, fan, label, square, again, soup, day, sit, ritual, thin, said, made, breast, style, starting, stone, contrast, already, conflict, role, survey, summit, central, famous, weapon, place, preparation, shower, position, cow, rank, ward, framework, water, formation, pant, voice, comfort, difficult, often, uncle, boyfriend, whatever, release, cluster, top, text, concern, attraction, basketball, equate, window, jazz, however, government, front, teenager, clip, device, bird, doctor, delay, herself, poll, but, sort, saw, trunk, series, sudden, speaker, cooperation, against, balance, sure, marketing, park, conservation, within, per, wilderness, them, pen, minister, inventory, civil, happen, oxygen, capability, source, pepper, fresh, hint, association, logic, buildup, class, slide, adoption, memory, courage, outside, cent, respondent, sum, collaboration, killer, health, recession, another, voter, cowboy, obstacle, challenge, woman, exposure, defendant, goal, continent, button, alarm, restaurant, rule, career, struggle, private, virtue, sometimes, seat, equation, fame, complaint, jump, compound, ran, buffalo, companion, afraid, least, strategy, surface, thumb, odds, lake, juror, understand, democracy, usually, sent, bus, democrat, description, editor, flag, former, observe, sin, implementation, ahead, territory, definition, knife, where, science, soft, chair, educator, painter, describe, banking, negotiation, mouth, done, decline, go, fabric, adolescent, lobby, tail, prepare, fall, build, title, partner, fish, grade, spoke, distant, reference, infection, dictionary, coach, change, admission, modern, devil, shout, force, rich, package, include, recent, fuel, slice, mine, scenario, citizen, mom, bite, menu, bench, poverty, publication, pizza, primary, pile, bed, development, reliability, thousand, address, grip, golf, campus, wait, portfolio, entertainment, perform, tennis, dean, mount, gather, metal, partnership, cover, talk, distribution, agency, column, inspection, secret, adjustment, variation, cake, still, rest, client, enjoy, cafe, demand, degree, gulf, revenue, consensus, combat, friend, accounting, dimension, yourself, wing, big, could, adviser, date, shell, crisis, campaign, small, watch, bear, burden, bullet, observation, soccer, murder, girlfriend, me, print, notice, match, sugar, pool, chord, warm, number, without, establish, snake, toward, airplane, salmon, favorite, director, do, quite, sand, information, cultural, mistake, away, youth, finance, discipline, statistics, detail, episode, celebrity, violence, zone, deep, achievement, miller, mission, evaluation, past, lot, resort, ten, reason, these, magazine, consciousness, review, auto, despite, captain, slow, think, videotape, replacement, during, cloud, slip, your, possible, decision, bat, exercise, burn, tomato, galaxy, counsel, alliance, operation, protest, particular, real, north, suffix, long, five, item, tear, train, display, instrument, strange, liquid, price, industry, operating, reservation, field, speed, baseball, sell, chairman, himself, pad, wide, bill, pot, fee, actor, slave, put, amendment, shape, divide, departure, effectiveness, doorway, according, lie, concert, born, background, technology, master, lung, emergency, arrangement, trick, preference, dry, message, player, maximum, accept, new, probably, population, corporation, acquisition, because, vehicle, effort, assembly, deputy, route, carpet, narrator, invent, salad, administration, resident, principle, monitor, garlic, six, clock, design, inmate, touchdown, college, reward, numeral, utility, gender, various, sing, come, training, cabinet, fortune, nurse, stranger, statue, complex, facility, fist, bishop, want, significance, dealer, planning, wear, wonder, strength, suffer, freshman, blow, ceremony, chaos, factor, officer, myself, needle, tiger, procedure, construction, credit, available, pursuit, freedom, claim, temple, acid, thought, alternative, pastor, govern, hell, cave, full, touch, exit, live, percentage, target, camp, middle, myth, provide, story, atom, responsibility, pause, pick, strong, lamp, lecture, value, indicate, agree, wildlife, hood, requirement, post, beat, operate, fit, business, impact, break, shopping, friendship, memorial, boundary, van, must, collapse, shock, initiative, gym, determination, jaw, payment, beginning, poetry, heavy, sail, electricity, inch, hold, sake, plane, drug, handful, circumstance, closet, reading, lord, suddenly, vision, ceiling, convention, dance, silent, gaze, absence, boulder, university, state, counterpart, presentation, fitness, harbor, legislature, independence, army, corridor, shall, civilization, direct, vegetable, similar, custom, perspective, expense, walk, identity, prosecution, buyer, kiss, knew, circuit, crystal, priest, loss, end, adult, basement, wind, prevent, awareness, total, battery, clerk, brush, evidence, circle, grave, support, model, relief, portrait, year, rose, file, meet, battle, pan, draw, swing, prediction, politician, cheek, relate, enforcement, mountain, flesh, house, hard, dawn, legal, pitch, estate, leave, camera, truth, prospect, tongue, fraud, recording, flat, edge, pet, arrange, which, evolution, ask, component, issue, horn, collect, quick, start, trading, finger, pass, minority, rider, refugee, light, offense, request, score, view, ground, abuse, participant, interesting, implication, shoot, stay, fact, letter, cousin, broad, angel, maker, allow, guideline, medium, period, under, show, ball, horizon, ridge, simple, pickup, ownership, protect, corner, gun, lap, curriculum, nominee, salary, bacteria, sound, half, singer, fine, uniform, clothe, statement, many, bathroom, food, program, reserve, net, quickly, characteristic, nerve, danger, walking, judgment, congress, equipment, grant, investor, exchange, setting, rate, laughter, quart, electric, earnings, wrote, makeup, benefit, architecture, motive, soul, assessment, pope, legacy, deficit, some, now, secretary, near, habitat, vary, true, damage, spring, rocket, shooting, pride, lion, hero, recently, compensation, bush, she, province, does, taxpayer, headquarters, profile, ban, nose, history, waste, member, mention, scandal, economics, base, document, miss, choose, dress, patient, consultant, selection, entire, ash, receiver, good, conference, mall, favor, medal, psychologist, seed, empire, meeting, cold, should, recognize, discover, season, impression, wage, belt, flood, came, rub, enemy, nature, male, begin, fork, rating, week, infrastructure, senior, bank, relation, pat, ballot, mean, line, attitude, read, tension, teaspoon, headline, innovation, mirror, award, thinking, authority, wave, cookie, core, ship, orientation, weekend, meal, entrance, figure, track, also, western, hand, cook, triangle, index, scheme, library, dose, music, ideology, office, couple, organ, shore, crew, although, executive, aunt, speech, integrity, examination, earthquake, nothing, condition, onto, input, article, majority, union, itself, oak, segment, joy, lift, sea, third, climb, nomination, wealth, anniversary, charity, free, fund, bought, contact, perhaps, highway, guy, universe, theater, they, pull, paint, rescue, foundation, alien, center, event, behavior, data, attempt, folk, river, advance, interaction, close, disorder, clothing, catch, berry, candle, type, sword, birthday, witness, family, early, discourse, pay, use, carbohydrate, rhetoric, bath, essence, late, tall, piece, car, beef, complete, compare, film, location, coalition, supply, potential, less, tree, while, would, sector, fiber, always, aid, distance, creek, laser, liberty, almost, nearly, level, hunter, exhibition, individual, time, settle, jet, smoke, easy, tape, second, entity, introduction, pc, scout"
    }
}
