# MIDIscript
A programming language, derived from Brainfuck, that reads a MIDI file and maps each note to a command.
## Usage
`java -jar MIDIscript.jar filename.midi`
## Commands
Each note on the keyboard is mapped to a command.
- `C` terminates the program
- `C#` increments the data pointer
- `D` decrements the data pointer
- `D#` increments the byte at the data pointer
- `E` decrements the byte at the data pointer
- `F` outputs the byte at the data pointer as an integer
- `F#` accepts one byte of input at the data pointer as an integer
- `G` outputs the byte at the data pointer as a character
- `G#` accepts one byte of input at the data pointer as a character
- `A` jumps the instruction pointer to the matching `A#` if the byte at the data pointer is zero
- `A#` jumps the instruction pointer the the matching `A` if the byte at the data pointer is nonzero
- `B` terminates the thread
