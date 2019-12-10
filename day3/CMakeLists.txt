cmake_minimum_required(VERSION 2.8)
project(aoc_day3)

if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE "Release" CACHE STRING
      "Choose the type of build, options are: Debug Release
RelWithDebInfo MinSizeRel."
      FORCE)
endif(NOT CMAKE_BUILD_TYPE)

add_definitions(-std=c++14)

add_executable(solution solution.cc)