#!/bin/zsh

rm outcome.bmp
rm result.bmp
rm test.bmp
rm graduate/modify.bmp
rm graduate/lw.bmp
rm graduate/B/newB.bmp
rm graduate/C/newC.bmp
rm MK.bmp
rm I_1.bmp
rm I_2.bmp
rm I_3.bmp
rm B_DCT.bmp
rm C_DCT.bmp
rm Fractal.bmp
rm graduate/encode.txt
rm graduate/A/encode.txt

for t in Range Domain
do
  rm graduate/$t/*.bmp
  rm out/production/demo/graduate/$t/*.bmp
done

for p in first second third forth
do
  rm graduate/A/Range/$p/*.bmp
  rm graduate/B/$p/*.bmp
  rm graduate/C/$p/*.bmp
  rm graduate/tamperDetection/A/Range/$p/*.bmp
  rm graduate/tamperDetection/B/$p/*.bmp
  rm graduate/tamperDetection/C/$p/*.bmp
done

for ((i=1; i < 9; i++))
do
    rm graduate/TnDomain/"$i"/*.bmp
    rm graduate/A/Domain/"$i"/first/*.bmp
    rm graduate/A/Domain/"$i"/second/*.bmp
    rm graduate/A/Domain/"$i"/third/*.bmp
    rm graduate/A/Domain/"$i"/forth/*.bmp
    rm graduate/tamperDetection/A/Domain/"$i"/first/*.bmp
    rm graduate/tamperDetection/A/Domain/"$i"/second/*.bmp
    rm graduate/tamperDetection/A/Domain/"$i"/third/*.bmp
    rm graduate/tamperDetection/A/Domain/"$i"/forth/*.bmp
    rm out/production/demo/graduate/TnDomain/"$i"/*.bmp
done