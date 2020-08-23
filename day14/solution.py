import argparse
import re
from collections import defaultdict
from typing import Dict
from typing import List
from typing import Tuple

REACTION_PATTERN = re.compile(r'^([0-9]+ [A-Z]+(?:, [0-9]+ [A-Z]+)*) => ([0-9]+) ([A-Z]+)+$')
INGREDIENT_PATTERN = re.compile(r'([0-9]+) ([A-Z]+)')
BASIC_CHEMICAL_NAME = 'ORE'
PRODUCT_NAME = 'FUEL'


class Chemical:
    allChemicals: Dict[str, 'Chemical'] = {}

    def __init__(self, name: str, outQuantity: int) -> None:
        self.name = name
        self.outQuantity = outQuantity
        self.ingredients: dict[str, int] = defaultdict(int)

    def __repr__(self) -> str:
        return f'<{self.name}>'

    def addIngredient(self, chemicalName: str, inputQuantity: int) -> None:
        self.ingredients[chemicalName] += inputQuantity

    def getRequiredIngredientsFor(self, quantity: int) -> Tuple[Dict[str, int], int]:
        """
        Returns the required chemicals and amounts to create `quantity` of
        this chemical. Also returns the exceed product quantity.
        """
        res: Dict[str, int] = {}
        minReactionsRequired = quantity // self.outQuantity + int(quantity % self.outQuantity > 0)
        exceedProductQuantity = minReactionsRequired * self.outQuantity - quantity
        for ingrName, ingrQuantity in self.ingredients.items():
            res[ingrName] = ingrQuantity * minReactionsRequired

        return res, exceedProductQuantity

    @classmethod
    def constructChemical(cls, rawEquation: str) -> 'Chemical':
        # Check if the equation is valid
        regexpObject = REACTION_PATTERN.search(rawEquation)
        if regexpObject is None:
            raise Exception(f'Invalid input ({rawEquation})')

        # The output portion, expected to be a new chemical
        outQuantity = int(regexpObject.group(2))
        outChemicalName = regexpObject.group(3)
        if outChemicalName in cls.allChemicals:
            raise Exception(f'{outChemicalName} existed before creation equation')
        outChemical = Chemical(outChemicalName, outQuantity)

        # Parse the resultant input portion
        inputEquation = regexpObject.group(1)
        for ingrObject in INGREDIENT_PATTERN.finditer(inputEquation):
            ingrQuantity = int(ingrObject.group(1))
            ingrChemicalName = ingrObject.group(2)

            # Add the ingredient cost to the output chemical
            outChemical.addIngredient(ingrChemicalName, ingrQuantity)

        return outChemical


# Add ORE
Chemical.allChemicals[BASIC_CHEMICAL_NAME] = Chemical(BASIC_CHEMICAL_NAME, 1)


class Solution:
    def __init__(self, inputFilePath: str) -> None:
        with open(inputFilePath, 'r') as fd:
            for line in fd:
                line = line.strip()
                if not line:
                    continue    # Skip blank lines
                newChemical = Chemical.constructChemical(line)
                Chemical.allChemicals[newChemical.name] = newChemical

    def solvePart1(self) -> int:
        sortedIngrList = self._sortChemicalEquations()
        requiredIngrs: Dict[str, int] = defaultdict(int)

        # Process the final equation creating `FUEL`
        # Add all ingredients from `FUEL`
        for ingr, ingrQuantity in Chemical.allChemicals[PRODUCT_NAME].ingredients.items():
            requiredIngrs[ingr] = ingrQuantity

        # Reversed engineer the ingredients to `ORE`
        for ingr in reversed(sortedIngrList[:-1]):
            if ingr not in requiredIngrs:
                raise Exception(f'{ingr} is not in currently processed equation')

            ingrQuantity = requiredIngrs[ingr]
            chemical = Chemical.allChemicals[ingr]
            requiredSubIngr, _ = chemical.getRequiredIngredientsFor(ingrQuantity)
            for subIngr, subIngrQuantity in requiredSubIngr.items():
                requiredIngrs[subIngr] += subIngrQuantity

        return requiredIngrs[BASIC_CHEMICAL_NAME]

    def _sortChemicalEquations(self) -> List[str]:
        """
        Returns a list containing all the chemical names by the order they
        should be created. Ignoring `ORE` in the result.
        """
        sortedIngrList: List[str] = []
        readiedIngrs = set([BASIC_CHEMICAL_NAME])
        unsortedIngrs: set[str] = set()
        for ingr in Chemical.allChemicals:
            unsortedIngrs.add(ingr)
        unsortedIngrs.remove(BASIC_CHEMICAL_NAME)

        while len(unsortedIngrs) > 0:
            for ingr in unsortedIngrs:
                chemical = Chemical.allChemicals[ingr]
                if all(subIngr in readiedIngrs for subIngr in chemical.ingredients):
                    sortedIngrList.append(ingr)
                    readiedIngrs.add(ingr)
                    unsortedIngrs.remove(ingr)
                    break

        return sortedIngrList


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Solution to day 14's problem")
    parser.add_argument('file', help='path to input file')
    args = parser.parse_args()

    sol = Solution(args.file)
    print(f'Part 1: {sol.solvePart1()}')
