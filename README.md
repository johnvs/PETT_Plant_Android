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
    Sound Responsive    0x20
    Rainbow Cycle 1     0x21
    Rainbow Cycle 2     0x22
    Rainbow Cycle 3     0x23
    Preset 1            0x24
    Preset 2            0x25
    Preset 3            0x26
    Preset 4            0x27
    Preset 5            0x28

    New Speed           0x30, <new speed>
    Run                 0x31, <color mode>
    Off                 0x32
    Pause               0x33
    Resume              0x34
    New Color Mode      0x35, <color mode>    Sent when color mode state = Running and
                                              a new color mode is selected from the list

  General
    Request Status      0x40                   0xC0
      Returns           0xC0,
                        Entrainment Mode Number,
                        Run/Stop button state,
                        Entrainment Pause/Resume button state,
                        Loop Checkbox state,
                        Color Mode,
                        Run/Off button state,
                        Color Pause/Resume button state,
                        Color Mode Speed

    End of command      0x00   << Don't use for a message

*********************************/
