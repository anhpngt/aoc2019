from collections import Counter
from typing import List

PUZZLE_INPUT = "284639-748759"


class Solution:
    def __init__(self, lowerLimit: int, upperLimit: int):
        self.lowerLimit = lowerLimit
        self.upperLimit = upperLimit

    def solvePart1(self) -> int:
        count = 0
        for number in range(self.lowerLimit, self.upperLimit + 1):
            if not self.hasSixDigits(number):
                continue

            digits = self.parseDigits(number)
            if self.hasIncreasingDigits(digits) \
                    and self.hasMatchingAdjacentDigits(digits):
                count += 1

        return count

    def solvePart2(self) -> int:
        count = 0
        for number in range(self.lowerLimit, self.upperLimit + 1):
            if not self.hasSixDigits(number):
                continue

            digits = self.parseDigits(number)
            if self.hasIncreasingDigits(digits) \
                    and self.satifiesTheComplicatedCriteria(digits):
                count += 1

        return count

    def parseDigits(self, number: int) -> List[int]:
        digits = []             # type: List[int]
        remainder = number
        while remainder > 0:
            remainder, digit = divmod(remainder, 10)
            digits.append(digit)

        return digits[::-1]

    def hasSixDigits(self, number: int) -> bool:
        return 100000 <= number <= 999999

    def hasIncreasingDigits(self, digits: List[int]) -> bool:
        for i in range(len(digits) - 1):
            if digits[i] > digits[i+1]:
                return False
        return True

    def hasMatchingAdjacentDigits(self, digits: List[int]) -> bool:
        # This methods assumes that the number (digits) is already
        # verified to be in increasing order
        ct = Counter(digits)
        return ct.most_common(1)[0][1] >= 2

    def satifiesTheComplicatedCriteria(self, digits: List[int]) -> bool:
        # This methods assumes that the number (digits) is already
        # verified to be in increasing order
        ct = Counter(digits)
        return 2 in ct.values()


if __name__ == "__main__":
    lowerLimit, upperLimit = map(int, PUZZLE_INPUT.split('-'))
    sol = Solution(lowerLimit, upperLimit)

    print('Part 1 Answer: {}'.format(sol.solvePart1()))
    print('Part 2 Answer: {}'.format(sol.solvePart2()))
