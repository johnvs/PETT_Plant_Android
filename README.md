This is an Android app designed to control a PETT Plant via Bluetooth.


/*********************************

  Pett Plant App Commands

 _Function_            _Command Number_
  Entrainment Mode
   Sequences
    Meditation          0x01
    Sleep               0x02
    Stay Awake          0x03

    Run                 0x10, <sequence #>       0x90
    Stop                0x11
    Pause               0x12
    Resume              0x13
    Loop On             0x14
    Loop Off            0x15

  Color Mode
    Sound Responsive      0x20
    Rainbow Loop All      0x21
    Rainbow Loop Whole    0x22
    Rainbow Loop Spectrum 0x23
    Around The World      0x24
    Random Pop            0x25
    Fck Yeah Colors       0x26
    Preset 4              0x27
    Preset 5              0x28

    New Speed           0x30, <new speed> (1 - 100)
    Run                 0x31, <color mode>
    Off                 0x32
    Pause               0x33
    Resume              0x34
    Set Color Mode      0x35, <color mode>    Sent when color mode state = Running and
                                              a new color mode is selected from the list

  General
    Request Status      0x40                   0xC0
      Returns           0xC0,
                        Entrainment Sequence,
                        Entrainment state (Stopped, Running or Paused),
                        Entrainment Loop On/Off,
                        Color Mode,
                        Color Mode state,
                        Color Mode Speed

    End of command      0x00   << Used internally to Plant controller.
                                  Don't use for a message

*********************************

  Pett Plant App Command Structure

  <NumBytes><CommandID><FirstDataByte> . . . <LastDataByte><Checksum>

  1. Number of Bytes to Follow
  2. Command ID
  3. Any data bytes associated with command
  4. Checksum, which equals the XOR of all previous bytes (in this case, bytes 1 - 3).

*********************************
