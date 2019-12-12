import argparse
from collections import deque
from typing import Deque
from typing import List
from typing import Optional

COM_NAME = 'COM'
SANTA_NAME = 'SAN'
YOUR_NAME = 'YOU'


class Object:
    def __init__(self, name: str, parent: Optional['Object']):
        self.name = name
        self.parent = parent        # Reference to the parent
        self.children = []          # Similarly to the children
        self.orbitCount = None      # type: Optional[int]

        if parent is not None:
            self.parent.children.append(self)

    def updateParent(self, parent) -> None:
        if self.parent:
            raise Exception('Object already has a parent')

        self.parent = parent
        parent.children.append(self)

    def updateOrbitCountFromParent(self) -> None:
        if self.parent:
            self.orbitCount = self.parent.orbitCount + 1
        else:
            self.orbitCount = 0

    def __repr__(self):
        parentName = self.parent.name if self.parent else None
        childrenNames = ','.join([child.name for child in self.children])
        return f'{parentName} <- {self.name} <- [{childrenNames}]'


class Solution:
    def __init__(self, inputFilePath: str):
        # Dictionary containing all the registered objects
        self.objects = {COM_NAME: Object(COM_NAME, None)}

        self._parseInput(inputFilePath)

    def solvePart1(self) -> int:
        totalOrbitCount = 0
        for obj in self.objects.values():
            totalOrbitCount += obj.orbitCount
        return totalOrbitCount

    def solvePart2(self) -> int:
        santaObj = self.objects[SANTA_NAME]
        yourObj = self.objects[YOUR_NAME]

        # Get a list of the object's ancestors, ordered from COM
        # to the object
        def getAllAncestors(obj: Object) -> List[Object]:
            res = []
            while obj.parent is not None:
                res.append(obj.parent)
                obj = obj.parent
            return res[::-1]

        santaAncestorList = getAllAncestors(santaObj)
        yourAncestorList = getAllAncestors(yourObj)

        # Determine the last common ancestor of both
        # and count the distance from that ancestor to both.
        lastCommonAncestorIdx = -1
        for santaAncestor, yourAncestor in zip(santaAncestorList, yourAncestorList):
            if santaAncestor == yourAncestor:
                lastCommonAncestorIdx += 1
            else:
                break
        return (len(santaAncestorList) - lastCommonAncestorIdx - 1) + (len(yourAncestorList) - lastCommonAncestorIdx - 1)

    def _parseInput(self, inputFilePath: str) -> None:
        with open(inputFilePath, 'r') as fd:
            for line in fd:
                # Register orbiter (and also orbitee if required)
                parentName, childName = line.rstrip('\n').split(')')
                if parentName not in self.objects:
                    self._registerNewObject(parentName, None)
                if childName not in self.objects:
                    self._registerNewObject(childName, self.objects[parentName])
                else:
                    self.objects[childName].updateParent(self.objects[parentName])

        # Update orbit count for each object
        objQueue = deque([self.objects[COM_NAME]])  # type: Deque[Object]
        while objQueue:
            obj = objQueue.popleft()
            obj.updateOrbitCountFromParent()
            objQueue.extend(obj.children)

    def _registerNewObject(self, name: str, parent: Optional[Object]) -> None:
        newChild = Object(name, parent)
        self.objects[name] = newChild


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Solution to day 6\'s problem of AoC')
    parser.add_argument('input', help='path to input file')

    args = parser.parse_args()
    sol = Solution(args.input)
    print(f'Part 1 Solution: {sol.solvePart1()}')
    print(f'Part 2 Solution: {sol.solvePart2()}')
