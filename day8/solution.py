import argparse
from itertools import product
from typing import List


def parseInputFile(inputFilePath: str) -> List[int]:
    with open(inputFilePath, 'r') as fd:
        rawInput = fd.readline().rstrip('\n')
        formattedInput = list(map(int, rawInput))
        return formattedInput


class Row:
    def __init__(self, rowInput: List[int] = []):
        self.data = rowInput      # type: List[int]

    def __repr__(self) -> str:
        return str(self.data)

    def countDigit(self, digit: int) -> int:
        return self.data.count(digit)


class Layer:
    def __init__(self, layerInput: List[int], width: int):
        self.ncol = width
        self.nrow, modCheck = divmod(len(layerInput), width)
        if modCheck != 0:
            raise RuntimeError(f'Invalid layer length ({len(layerInput)}) '
                               f'for specified width ({width})')

        self.data = []      # type: List[Row]
        for ir in range(self.nrow):
            newRow = Row(layerInput[self.ncol*ir:self.ncol*(ir+1)])
            self.data.append(newRow)

    def get(self, row: int, col: int) -> int:
        return self.data[row].data[col]

    def countDigit(self, digit: int) -> int:
        return sum([row.countDigit(digit) for row in self.data])

    def __repr__(self) -> str:
        return str([row for row in self.data])


class Image:
    BLACK = 0
    WHITE = 1
    TRANSPARENT = 2

    def __init__(self, imageInput: List[int], width: int = 25, height: int = 6):
        self.ncol = width
        self.nrow = height
        self.nlayer, modCheck = divmod(len(imageInput), width * height)
        if modCheck != 0:
            raise RuntimeError(f'Invalid image input length ({len(imageInput)}) '
                               f'for specified width ({width}) and height ({height})')

        # Parse the input into layers and rows
        layerSize = self.ncol * self.nrow
        self.data = []      # type: List[Layer]
        for il in range(self.nlayer):
            newLayer = Layer(imageInput[layerSize*il:layerSize*(il+1)], self.ncol)
            self.data.append(newLayer)

    def get(self, layer: int, row: int, col: int) -> int:
        return self.data[layer].get(row, col)

    def render(self) -> str:
        # Empty image
        result = [[2 for _ in range(self.ncol)] for _ in range(self.nrow)]

        # Iterate through and render each pixel
        for x, y in product(range(self.nrow), range(self.ncol)):
            for z in range(self.nlayer):
                pixelVal = self.get(z, x, y)
                if pixelVal != self.TRANSPARENT:
                    result[x][y] = pixelVal
                    break

        # Convert 0/1 values to #/space for better visualization
        # then return the string result
        visualization = [
            [' ' if x == self.BLACK else '#' for x in row] for row in result
        ]
        return '\n'.join([''.join(row) for row in visualization])

    def __repr__(self) -> str:
        return str([layer for layer in self.data])


class Solution:
    def __init__(self, inputFilePath):
        imageInput = parseInputFile(inputFilePath)
        self.image = Image(imageInput)

    def solvePart1(self) -> int:
        lowestZeroCount = 1 << 31
        countIdx = -1
        for i in range(self.image.nlayer):
            zeroCount = self.image.data[i].countDigit(0)
            if zeroCount < lowestZeroCount:
                lowestZeroCount = zeroCount
                countIdx = i

        chosenLayer = self.image.data[countIdx]
        return chosenLayer.countDigit(1) * chosenLayer.countDigit(2)

    def solvePart2(self) -> str:
        return self.image.render()


if __name__ == "__main__":
    parser = argparse.ArgumentParser('Solution to day 8\'s problem')
    parser.add_argument('input', help='path to input file')
    args = parser.parse_args()

    sol = Solution(args.input)
    print(f'Part 1 solution: {sol.solvePart1()}')
    print(f'Part 2 solution:\n{sol.solvePart2()}')
