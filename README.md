# gw
A gradlew/gradle wrapper.

![build](https://github.com/gschrader/gw/workflows/release/badge.svg)
[![GitHub release](https://img.shields.io/github/release/gschrader/gw.svg)](https://github.com/gschrader/gw/releases)
[![License](https://img.shields.io/github/license/gschrader/gw.svg)](https://raw.githubusercontent.com/gschrader/gw/master/LICENSE)

A Java version of [gdub](https://github.com/dougborg/gdub) so it can be used on Windows (macOS and Linux binary provided as well).

## Usage

- `gw [gradle task name]`
- `gw --gw --help`
- `gw --gw -V`

## Installation

Download exe from [releases](https://github.com/gschrader/gw/releases) and add to your path, or install the MSI which will install and add to your path for you.

## FAQ

#### Why did you create this
I'm so used to using [gdub](https://github.com/dougborg/gdub) on macOS but my work uses Windows and I missed using `gw`. There are also a couple of
issues [22](https://github.com/dougborg/gdub/issues/22) [32](https://github.com/dougborg/gdub/issues/32) that I was able to fix. 

I found a [go](https://github.com/srs/gw) version which allowed me to use on Windows but it has a bug that hasn't 
been [merged](https://github.com/srs/gw/pull/1) so I presume the project is dead.

A [powershell](https://github.com/dougborg/gdub/pull/35/files) version might have made more sense, but I'm a Java dev and am far more proficient working with Java.

I wanted an excuse to try [Graal](https://www.graalvm.org/docs/reference-manual/native-image/) native images and
[picocli](https://picocli.info). 

#### Should I use this instead of gdub
Probably not if you're happy with gdub.