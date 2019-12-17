import argparse
import math
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
        """Returns Euclidean point-to-origin distance"""
        return math.sqrt(self.x ** 2 + self.y ** 2)

    def isAt(self, x: int, y: int) -> bool:
        return self.x == x and self.y == y

    def getLineOfSight(self) -> 'LineOfSight':
        return LineOfSight(self.x, self.y)

    def getAngleFrom(self, o: 'Point') -> float:
        """Return the angle w.r.t `o`, clockwise-positive,
        upward-origin (aligns with negative y-direction).
        The value ranges from 0 to 2 Pi."""
        val = math.atan2(self.x - o.x, o.y - self.y)
        return val if val >= 0 else val + 2 * math.pi

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
            xygcd = math.gcd(x, y)
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
        return len(self.getAsteroidDetectedAtPoint(x, y))

    def getAsteroidDetectedAt(self, asteroid: 'Point') -> List[Point]:
        return self.getAsteroidDetectedAtPoint(asteroid.x, asteroid.y)

    def getAsteroidDetectedAtPoint(self, x: int, y: int) -> List[Point]:
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

        # Remove the introduced offset from the detected asteroids
        # and return the list
        return [ast.offsetFrom(-x, -y) for ast in detected.values()]

    def removeAsteroids(self, asteroids: List[Point]) -> None:
        for asteroid in asteroids:
            self.allAsteroids.remove(asteroid)

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

    def solvePart2(self, stationAsteroid: Point, betNumber: int) -> int:
        # Remove the chosen asteroid
        self.starmap.removeAsteroids([stationAsteroid])

        # Iteratively
        count = 0
        while self.starmap:
            # The asteroids being destroy at current laser rotation
            detectedAsteroids = self.starmap.getAsteroidDetectedAt(stationAsteroid)

            # Skip to next rotation if betNumber is not within current rotation
            if betNumber > count + len(detectedAsteroids):
                count += len(detectedAsteroids)
                self.starmap.removeAsteroids(detectedAsteroids)
                continue

            # Sort the detected asteroid according to their line of sight
            sortedAsteroids = sorted(detectedAsteroids,
                                     key=lambda a: a.getAngleFrom(stationAsteroid))
            bettedAsteroid = sortedAsteroids[betNumber - count - 1]
            return bettedAsteroid.x * 100 + bettedAsteroid.y

        raise RuntimeError('Failed to solve for answer')


if __name__ == "__main__":
    parser = argparse.ArgumentParser('Solution to day 9\'s problem')
    parser.add_argument('input', help='path to input file')
    args = parser.parse_args()

    sol = Solution(args.input)
    print(f'Part 1 solution: {sol.solvePart1()}')
    # Part 1 solution is asteroid at (x=22,y=19)
    print(f'Part 2 solution: {sol.solvePart2(Point(22, 19), 200)}')
