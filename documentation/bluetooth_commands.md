# Bluetooth Commands
This is a list of the commands that are sent via Bluetooth to a PETT Plant LED sculpture in order to control it.
<!-- These commands are sent via bluetooth to the Teensy microcontroller in the LED sculpture. -->

### Command Structure
Commands have the following structure.

```<NumBytes><CommandID><FirstDataByte>...<LastDataByte><Checksum>```

1. Number of Bytes to Follow
2. Command ID
3. Any data bytes associated with command
4. Checksum, which equals the XOR of all previous bytes (i.e. NumBytes - LastDataByte).

### Entrainment Mode Commands
| Command  | Code                       |
|----------|----------------------------|
| Run      | `0x10, <sequence #>, 0x90` |
| Stop     | `0x11`                     |
| Pause    | `0x12`                     |
| Resume   | `0x13`                     |
| Loop On  | `0x14`                     |
| Loop Off | `0x15`                     |

### Entrainment Mode Sequences
| Sequence   | Sequence No. |
|------------|--------------|
| Meditation | `0x01`       |
| Sleep      | `0x02`       |
| Stay Awake | `0x03`       |

### Color Mode Commands
| Command        | Code                 | Comments        |
|----------------|----------------------|-----------------|
| New Speed      | `0x30, <speed>`      | Speed = 1 - 100 |
| Run            | `0x31, <color mode>` |                 |
| Off            | `0x32`               |                 |
| Pause          | `0x33`               |                 |
| Resume         | `0x34`               |                 |
| Color Mode     | `0x35, <color mode>` | Sent when color mode state = `Running` and a new color mode is selected from the list |

### Color Modes
| Color Mode            | Mode No. |
|-----------------------|----------|
| Sound Responsive      | `0x20`   |
| Rainbow Loop All      | `0x21`   |
| Rainbow Loop Whole    | `0x22`   |
| Rainbow Loop Spectrum | `0x23`   |
| Around The World      | `0x24`   |
| Random Pop            | `0x25`   |
| Fck Yeah Colors       | `0x26`   |
| Preset 4              | `0x27`   |
| Preset 5              | `0x28`   |

### General Commands
| Command               | Code    | Comments |
|-----------------------|---------|----------|
| Request Status        | `0x40`  |          |
| Request State Returns | `0xC0 <Entrainment Sequence>, <Entrainment State>, <Entrainment Loop>, <Color Mode>, <Color Mode State>, <Color Mode Speed>` | Entrainment state = `Stopped`, `Running` or `Paused`. Entrainment Loop = `On` or `Off`. |
| End_of_command        | `0x00`  | Used internally to Plant controller. Don't use for a message |
