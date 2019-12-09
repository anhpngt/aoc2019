#include <fstream>
#include <iostream>
#include <string>

using std::cout;
using std::endl;

class Solution
{
public:
    int solve(const std::string &inputFilePath)
    {
        std::ifstream inputFile(inputFilePath);
        if (!inputFile.is_open())
        {
            throw std::invalid_argument("Unable to open specified input file.");
        }

        std::string line;
        int totalFuelRequired = 0;
        while (getline(inputFile, line))
        {
            int moduleMass = std::stoi(line);
            totalFuelRequired += this->calculateFuelRequired(moduleMass);
        }

        return totalFuelRequired;
    }

private:
    int calculateFuelRequired(int moduleMass)
    {
        return (moduleMass / 3) - 2;
    }
};

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        cout << "Please provide the puzzle input file\n"
                "E.g :./solution path/to/puzzle_input.txt"
             << endl;
    }

    cout << "Solution: " << Solution().solve(argv[1]) << " kg" << endl;
}