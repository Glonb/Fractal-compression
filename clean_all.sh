#!/bin/zsh

rm outcome.bmp
rm compressed.bmp
rm modify.bmp
rm graduate/Domain/*.bmp
rm graduate/Range/*.bmp
rm graduate/A/Range/first/*.bmp
rm graduate/A/Range/second/*.bmp
rm graduate/A/Range/third/*.bmp
rm graduate/A/Range/forth/*.bmp
rm graduate/B/first/*.bmp
rm graduate/B/second/*.bmp
rm graduate/B/third/*.bmp
rm graduate/B/forth/*.bmp
rm graduate/C/first/*.bmp
rm graduate/C/second/*.bmp
rm graduate/C/third/*.bmp
rm graduate/C/forth/*.bmp
rm graduate/encode.txt
rm graduate/A/encode.txt
rm out/production/demo/graduate/Range/*.bmp
rm out/production/demo/graduate/Domain/*.bmp


for ((i=1; i < 9; i++)); do
    rm graduate/TnDomain/"$i"/*.bmp
    rm graduate/A/Domain/"$i"/first/*.bmp
    rm graduate/A/Domain/"$i"/second/*.bmp
    rm graduate/A/Domain/"$i"/third/*.bmp
    rm graduate/A/Domain/"$i"/forth/*.bmp
    rm out/production/demo/graduate/TnDomain/"$i"/*.bmp
done