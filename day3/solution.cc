#include <fstream>
#include <iostream>
#include <iterator>
#include <string>
#include <sstream>
#include <vector>

using std::cout;
using std::endl;

bool isBetween(int x, int a, int b)
{
    if (a >= b)
    {
        return a >= x && x >= b;
    }
    else
    {
        return b >= x && x >= a;
    }
}

/**
 * This class represents the original data elements (e.g. U7, R6...),
 * mainly used for parsing from string to values.
 */
class Path
{
public:
    char direction;
    int distance;

    Path(const std::string &pathStr)
    {
        if (std::sscanf(pathStr.c_str(), "%c%d", &this->direction, &this->distance) != 2)
        {
            // 2 elements are always expected
            throw std::runtime_error("Invalid path (" + pathStr + ")");
        }
    }

    friend std::ostream &operator<<(std::ostream &os, const Path &p);
};

std::ostream &operator<<(std::ostream &os, const Path &p)
{
    os << p.direction << "-" << p.distance;
    return os;
}

/**
 * Cartesians coordinates on the grid.
 * x-direction towards right, y-direction towards up.
 */
class Point
{
public:
    int x;
    int y;

    Point() {}

    Point(int x, int y)
    {
        this->x = x;
        this->y = y;
    }

    Point(const Point &startPoint, const Path &path)
    {
        int xOffset, yOffset;
        switch (path.direction)
        {
        case 'U':
            xOffset = 0;
            yOffset = path.distance;
            break;
        case 'D':
            xOffset = 0;
            yOffset = -path.distance;
            break;
        case 'R':
            xOffset = path.distance;
            yOffset = 0;
            break;
        case 'L':
            xOffset = -path.distance;
            yOffset = 0;
            break;
        default:
            throw std::runtime_error(std::string("Invalid direction (") + path.direction + ")");
        }

        this->x = startPoint.x + xOffset;
        this->y = startPoint.y + yOffset;
    }

    int manhattanDistToPoint(const Point &other) const
    {
        return abs(this->x - other.x) + abs(this->y - other.y);
    }

    int manhattanDistToOrigin() const
    {
        return abs(this->x) + abs(this->y);
    }

    friend std::ostream &operator<<(std::ostream &os, const Point &p);
};

std::ostream &operator<<(std::ostream &os, const Point &p)
{
    os << "(" << p.x << ", " << p.y << ")";
    return os;
}

/**
 * This class contains the coordinates of the two ends
 * of a straight section of a wire.
 */
class Line
{
public:
    Point start, end;
    bool isHorizontal;
    int length;

    Line(const Point &startPoint, const Path &path)
    {
        this->start = startPoint;
        this->end = Point(startPoint, path);
        this->length = path.distance;
        this->isHorizontal = path.direction == 'R' || path.direction == 'L';
    }

    /**
     * Check if two lines intersect eachother (and determine the 
     * intersection if they do)
     */
    bool intersects(const Line &other, Point &intersectionPoint) const
    {
        // If two lines are parallel, return false immediately?
        if (this->isHorizontal == other.isHorizontal)
        {
            return false;
        }

        const Line &hLine = this->isHorizontal ? *this : other;
        const Line &vLine = this->isHorizontal ? other : *this;

        if (isBetween(hLine.start.y, vLine.start.y, vLine.end.y) && isBetween(vLine.start.x, hLine.start.x, hLine.end.x))
        {
            intersectionPoint.x = vLine.start.x;
            intersectionPoint.y = hLine.start.y;
            return true;
        }

        return false;
    }

    friend std::ostream &operator<<(std::ostream &os, const Line &l);
};

std::ostream &operator<<(std::ostream &os, const Line &l)
{
    os << "[" << l.start << "->" << l.end << "]";
    return os;
}

/**
 * A wire contains a collections of lines
 */
class Wire
{
public:
    std::vector<Line> data;

    Wire() {}

    void addLine(const Line &newLine)
    {
        this->data.push_back(newLine);
    }

    friend std::ostream &operator<<(std::ostream &os, const Wire &w);
};

std::ostream &operator<<(std::ostream &os, const Wire &w)
{
    os << "[";
    if (!w.data.empty())
    {
        std::copy(w.data.begin(), w.data.end(), std::ostream_iterator<Line>(os, ", "));
        os << "\b\b";
    }
    os << "]";
    return os;
}

class Solution
{
public:
    Solution(const std::string &inputFilePath)
    {
        this->parseInput(inputFilePath);
    }

    int solvePart1()
    {
        int shortestDistanceToOrigin = INT32_MAX;
        Point intersectionPoint;
        for (const Line &l1 : wires[0].data)
            for (const Line &l2 : wires[1].data)
            {
                if (l1.intersects(l2, intersectionPoint))
                {
                    int distanceToOrigin = intersectionPoint.manhattanDistToOrigin();
                    if (distanceToOrigin < shortestDistanceToOrigin)
                    {
                        shortestDistanceToOrigin = distanceToOrigin;
                    }
                }
            }
        return shortestDistanceToOrigin;
    }

    int solvePart2()
    {
        int shortestStepDistance = INT32_MAX;
        Point intersectionPoint;
        int previousLinesStepDistance1 = 0;
        for (const Line &l1 : wires[0].data)
        {
            int previousLinesStepDistance2 = 0;
            for (const Line &l2 : wires[1].data)
            {
                if (l1.intersects(l2, intersectionPoint))
                {
                    int totalStepDistance1 = previousLinesStepDistance1 + l1.start.manhattanDistToPoint(intersectionPoint);
                    int totalStepDistance2 = previousLinesStepDistance2 + l2.start.manhattanDistToPoint(intersectionPoint);
                    int totalStepDistance = totalStepDistance1 + totalStepDistance2;
                    if (totalStepDistance < shortestStepDistance)
                    {
                        shortestStepDistance = totalStepDistance;
                    }
                }

                previousLinesStepDistance2 += l2.length;
            }

            previousLinesStepDistance1 += l1.length;
        }

        return shortestStepDistance;
    }

private:
    std::vector<Wire> wires;

    void initWireData(const int numberOfWires)
    {
        wires.clear();
        for (int i = 0; i < numberOfWires; i++)
        {
            wires.push_back(Wire());
        }
    }

    void parseInput(const std::string &inputFilePath, const int numberOfWires = 2)
    {
        std::ifstream inputFile(inputFilePath);
        if (!inputFile.is_open())
        {
            throw std::invalid_argument("Unable to open specified input file.");
        }

        this->initWireData(numberOfWires);
        std::string line;

        // Parse each line (correspond to a wire) in the puzzle input file
        for (int i = 0; i < numberOfWires; i++)
        {
            if (!getline(inputFile, line))
            {
                throw std::runtime_error("Missing data for wire number " + (i + 1));
            }

            Point startPoint(0, 0);

            // Parse the coordinates of the wire that are comma separated
            std::string pathStr;
            std::istringstream ss(line);
            while (getline(ss, pathStr, ','))
            {
                Path newPath(pathStr);
                Line newLine(startPoint, newPath);
                this->wires[i].addLine(newLine);

                startPoint = newLine.end;
            }
        }
    }
};

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        cout << "Please provide puzzle input file.\n"
                "E.g: ./part1 path/to/input.txt"
             << endl;
        return EXIT_FAILURE;
    }

    std::string inputFilePath = argv[1];
    Solution sol(inputFilePath);
    cout << "Solution:\n"
         << "Part 1: " << sol.solvePart1() << "\n"
         << "Part 2: " << sol.solvePart2() << endl;
    return EXIT_SUCCESS;
}
