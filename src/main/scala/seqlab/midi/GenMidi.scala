package seqlab.midi

object GenMidi {
  object Notes {
    def a0: MidiNote = MidiNote(9)
    def b0: MidiNote = MidiNote(11)
    def c0: MidiNote = MidiNote(12)
    def d0: MidiNote = MidiNote(14)
    def e0: MidiNote = MidiNote(16)
    def f0: MidiNote = MidiNote(17)
    def g0: MidiNote = MidiNote(19)
    def a1: MidiNote = MidiNote(21)
    def b1: MidiNote = MidiNote(23)
    def c1: MidiNote = MidiNote(24)
    def d1: MidiNote = MidiNote(26)
    def e1: MidiNote = MidiNote(28)
    def f1: MidiNote = MidiNote(29)
    def g1: MidiNote = MidiNote(31)
    def a2: MidiNote = MidiNote(33)
    def b2: MidiNote = MidiNote(35)
    def c2: MidiNote = MidiNote(36)
    def d2: MidiNote = MidiNote(38)
    def e2: MidiNote = MidiNote(40)
    def f2: MidiNote = MidiNote(41)
    def g2: MidiNote = MidiNote(43)
    def a3: MidiNote = MidiNote(45)
    def b3: MidiNote = MidiNote(47)
    def c3: MidiNote = MidiNote(48)
    def d3: MidiNote = MidiNote(50)
    def e3: MidiNote = MidiNote(52)
    def f3: MidiNote = MidiNote(53)
    def g3: MidiNote = MidiNote(55)
    def a4: MidiNote = MidiNote(57)
    def b4: MidiNote = MidiNote(59)
    def c4: MidiNote = MidiNote(60)
    def d4: MidiNote = MidiNote(62)
    def e4: MidiNote = MidiNote(64)
    def f4: MidiNote = MidiNote(65)
    def g4: MidiNote = MidiNote(67)
    def a5: MidiNote = MidiNote(69)
    def b5: MidiNote = MidiNote(71)
    def c5: MidiNote = MidiNote(72)
    def d5: MidiNote = MidiNote(74)
    def e5: MidiNote = MidiNote(76)
    def f5: MidiNote = MidiNote(77)
    def g5: MidiNote = MidiNote(79)
    def a6: MidiNote = MidiNote(81)
    def b6: MidiNote = MidiNote(83)
    def c6: MidiNote = MidiNote(84)
    def d6: MidiNote = MidiNote(86)
    def e6: MidiNote = MidiNote(88)
    def f6: MidiNote = MidiNote(89)
    def g6: MidiNote = MidiNote(91)
    def a7: MidiNote = MidiNote(93)
    def b7: MidiNote = MidiNote(95)
    def c7: MidiNote = MidiNote(96)
    def d7: MidiNote = MidiNote(98)
    def e7: MidiNote = MidiNote(100)
    def f7: MidiNote = MidiNote(101)
    def g7: MidiNote = MidiNote(103)
    def a8: MidiNote = MidiNote(105)
    def b8: MidiNote = MidiNote(107)
    def c8: MidiNote = MidiNote(108)
    def d8: MidiNote = MidiNote(110)
    def e8: MidiNote = MidiNote(112)
    def f8: MidiNote = MidiNote(113)
    def g8: MidiNote = MidiNote(115)
  }

  object Drums {
    def highQ: MidiNote = MidiNote(27)
    def slap: MidiNote = MidiNote(28)
    def recordScratch1: MidiNote = MidiNote(29)
    def recordScratch2: MidiNote = MidiNote(30)
    def sticks: MidiNote = MidiNote(31)
    def click1: MidiNote = MidiNote(32)
    def click2: MidiNote = MidiNote(33)
    def smallBell: MidiNote = MidiNote(34)
    def acousticBassDrum: MidiNote = MidiNote(35)
    def bassDrum1: MidiNote = MidiNote(36)
    def sideStick: MidiNote = MidiNote(37)
    def acousticSnare: MidiNote = MidiNote(38)
    def handClap: MidiNote = MidiNote(39)
    def electricSnare: MidiNote = MidiNote(40)
    def lowFloorTom: MidiNote = MidiNote(41)
    def closedHighHat: MidiNote = MidiNote(42)
    def highFloorTom: MidiNote = MidiNote(43)
    def pedalHighHat: MidiNote = MidiNote(44)
    def lowTom: MidiNote = MidiNote(45)
    def openHighHat: MidiNote = MidiNote(46)
    def lowmidTom: MidiNote = MidiNote(47)
    def highmidTom: MidiNote = MidiNote(48)
    def crashCymbal1: MidiNote = MidiNote(49)
    def highTom: MidiNote = MidiNote(50)
    def rideCymbal1: MidiNote = MidiNote(51)
    def chineseCymbal: MidiNote = MidiNote(52)
    def rideBell: MidiNote = MidiNote(53)
    def tambourine: MidiNote = MidiNote(54)
    def splashCymbal: MidiNote = MidiNote(55)
    def cowbell: MidiNote = MidiNote(56)
    def crashCymbal2: MidiNote = MidiNote(57)
    def vibraslap: MidiNote = MidiNote(58)
    def rideCymbal2: MidiNote = MidiNote(59)
    def highBongo: MidiNote = MidiNote(60)
    def lowBongo: MidiNote = MidiNote(61)
    def muteHighConga: MidiNote = MidiNote(62)
    def openHighConga: MidiNote = MidiNote(63)
    def lowConga: MidiNote = MidiNote(64)
    def highTimbale: MidiNote = MidiNote(65)
    def lowTimbale: MidiNote = MidiNote(66)
    def highAgogo: MidiNote = MidiNote(67)
    def lowAgogo: MidiNote = MidiNote(68)
    def cabasa: MidiNote = MidiNote(69)
    def maracas: MidiNote = MidiNote(70)
    def shortWhistle: MidiNote = MidiNote(71)
    def longWhistle: MidiNote = MidiNote(72)
    def shortGuiro: MidiNote = MidiNote(73)
    def longGuiro: MidiNote = MidiNote(74)
    def claves: MidiNote = MidiNote(75)
    def highWoodBlock: MidiNote = MidiNote(76)
    def lowWoodBlock: MidiNote = MidiNote(77)
    def muteCuica: MidiNote = MidiNote(78)
    def openCuica: MidiNote = MidiNote(79)
    def muteTriangle: MidiNote = MidiNote(80)
    def openTriangle: MidiNote = MidiNote(81)
    def shaker: MidiNote = MidiNote(82)
    def sleighBells: MidiNote = MidiNote(83)
    def bellTree: MidiNote = MidiNote(84)
    def castanets: MidiNote = MidiNote(85)
    def muteSudro: MidiNote = MidiNote(86)
    def openSudro: MidiNote = MidiNote(87)
  }
}
