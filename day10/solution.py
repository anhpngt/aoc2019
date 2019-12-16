import argparse
from math import gcd
from math import sqrt
from typing import Dict
from typing import List


class Point:
    def __init__(self, x: int, y: int):
        self.x = x
        self.y = y

    def __repr__(self) -> str:
        return f'Point({self.x},{self.y})'

    def __hash__(self) -> int:
        return hash((self.x, self.y))

    def __eq__(self, o: 'Point') -> bool:
        return self.x == o.x and self.y == o.y

    def distance(self):
        # Returns Euclidean point-to-origin distance
        return sqrt(self.x ** 2 + self.y ** 2)

    def isAt(self, x: int, y: int) -> bool:
        return self.x == x and self.y == y

    def getLineOfSight(self) -> 'LineOfSight':
        return LineOfSight(self.x, self.y)

    def offsetFrom(self, x: int, y: int) -> 'Point':
        # Returns Point which considers (x, y) as the origin instead
        return Point(self.x - x, self.y - y)


class LineOfSight:
    def __init__(self, x: int, y: int):
        assert not(x == 0 and y == 0)

        if x == 0:
            self.x = 0      # type: int
            self.y = 1 if y > 0 else -1
        elif y == 0:
            self.y = 0      # type: int
            self.x = 1 if x > 0 else -1
        else:
            # both x, y != 0
            xygcd = gcd(x, y)
            self.x = x // xygcd
            self.y = y // xygcd

    def __hash__(self) -> int:
        return hash((self.x, self.y))

    def __eq__(self, o: 'LineOfSight') -> bool:
        return self.x == o.x and self.y == o.y


class StarMap:
    def __init__(self, inputArray: List[str]):
        self.height = len(inputArray)       # max y-direction
        self.width = len(inputArray[0])     # max x-direction

        self.allAsteroids = set()        # type: set[Point]
        for y in range(self.height):
            for x in range(self.width):
                if inputArray[y][x] == '#':
                    self.allAsteroids.add(Point(x, y))

    def countAsteroidDetectedAt(self, asteroid: 'Point') -> int:
        return self.countAsteroidDetectedAtPoint(asteroid.x, asteroid.y)

    def countAsteroidDetectedAtPoint(self, x: int, y: int) -> int:
        # A collection keeping all the asteroids that can be detected
        # and their line-of-sight "angles"
        detected = {}       # type: Dict[LineOfSight, Point]

        # iterate through each asteroid
        for asteroid in self.allAsteroids:
            # skip the asteroid which is being chosen
            if asteroid.isAt(x, y):
                continue

            # Check if the asteroid is blocked by or blocks
            # any registered asteroid
            asteroidFromPoint = asteroid.offsetFrom(x, y)
            asteroidLoS = asteroidFromPoint.getLineOfSight()
            if asteroidLoS in detected:
                # If blocked, simply skip
                if asteroidFromPoint.distance() > detected[asteroidLoS].distance():
                    continue

            # If the asteroid is not blocked, register it
            # This may override another asteroid in the same line-of-sight
            detected[asteroidLoS] = asteroidFromPoint

        return len(detected)

    @classmethod
    def parseInputFile(cls, inputFilePath: str) -> 'StarMap':
        with open(inputFilePath, 'r') as fd:
            lines = fd.readlines()      # type: List[str]
            lines = list(map(lambda s: s.rstrip('\n'), lines))

        return cls(lines)


class Solution:
    def __init__(self, inputFilePath: str):
        self.starmap = StarMap.parseInputFile(inputFilePath)

    def solvePart1(self) -> int:
        return max(
            self.starmap.countAsteroidDetectedAt(asteroid)
            for asteroid in self.starmap.allAsteroids
        )


if __name__ == "__main__":
    parser = argparse.ArgumentParser('Solution to day 9\'s problem')
    parser.add_argument('input', help='path to input file')
    args = parser.parse_args()

    sol = Solution(args.input)
    print(f'Part 1 solution: {sol.solvePart1()}')
