import argparse


class Solution:
    OPCODE_ADD = 1
    OPCODE_MUL = 2
    OPCODE_HALT = 99

    def solve(self, inputFilePath: str) -> int:
        with open(inputFilePath, 'r') as fd:
            # Reads from file, split each opcode by comma ","
            # then convert each element to integer
            intcodes = list(map(int, fd.readline().split(',')))

        # According to the problem, we need to do some value replacements
        intcodes[1] = 12
        intcodes[2] = 2

        # Start running the program
        oppos = 0
        opcode = intcodes[oppos]
        while opcode != self.OPCODE_HALT:
            input1 = intcodes[intcodes[oppos + 1]]
            input2 = intcodes[intcodes[oppos + 2]]
            outputPos = intcodes[oppos + 3]

            if opcode == self.OPCODE_ADD:
                result = input1 + input2
            elif opcode == self.OPCODE_MUL:
                result = input1 * input2
            else:
                raise RuntimeError('Invalid OPCODE: {}'.format(opcode))

            intcodes[outputPos] = result

            # Next operation
            oppos = oppos + 4
            opcode = intcodes[oppos]

        return intcodes[0]


if __name__ == "__main__":
    parser = argparse.ArgumentParser('Solution to day 2\'s problem')
    parser.add_argument('input', help='path to input file')
    args = parser.parse_args()

    print('Solution: {}'.format(Solution().solve(args.input)))
