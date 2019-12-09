import argparse
from typing import *


class Solution:
    def solve(self, inputFile: str) -> int:
        totalFuelRequired = 0

        with open(inputFile, 'r') as fd:
            for line in fd.readlines():
                moduleMass = int(line)
                totalFuelRequired += self._calculateFuelRequired(moduleMass)

        return totalFuelRequired

    def _calculateFuelRequired(self, moduleMass: int) -> int:
        def eqn(x):
            return (x // 3) - 2

        fuelRequired = 0
        while moduleMass > 8:
            moduleMass = eqn(moduleMass)
            fuelRequired += moduleMass

        return fuelRequired


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Solution to day 1, part2\'s problem of AoC')
    parser.add_argument('input', help='path to input file')

    args = parser.parse_args()
    print('Solution: {} kg'.format(Solution().solve(args.input)))
