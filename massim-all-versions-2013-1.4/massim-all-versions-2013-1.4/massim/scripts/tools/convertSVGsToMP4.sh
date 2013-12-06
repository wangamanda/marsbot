NUM=0
QUEUE=""

function queue {
	QUEUE="$QUEUE
$1"
	NUM=$(($NUM+1))
}

function dequeue {
	OLDDEQUEUE=$QUEUE
	QUEUE=""
	for PID in $OLDDEQUEUE
	do
		if [ ! "$PID" = "$1" ] ; then
			QUEUE="$QUEUE
$PID"
		fi
	done
	NUM=$(($NUM-1))
}

function checkqueue {
	OLDCHQUEUE=$QUEUE
	for PID in $OLDCHQUEUE
	do
		if [ ! -d /proc/$PID ] ; then
			dequeue $PID
		fi
	done
}


folder=$( pwd )

if [ ! -d svgs ]
then
  mkdir -p svgs
  sed -e "s/<\/svg>//g" masSim-0.svg > svgs/0.svg
  count=0
  for i in $( ls -1 masSim-*svg ); do
    if [ $i != masSim-0.svg ]
    then
      j=${i:7}
      echo $count :: $i :: $j
      cp svgs/0.svg svgs/$j && xml_grep --root "svg/g[@id='scaleSvg']" --wrap '' $i >> svgs/$j && echo "</svg>" >> svgs/$j &
      queue $!
      count=`expr $count + 1`
      while [ $NUM -ge 50 ] # MAX PROCESSES
        do
          checkqueue
          sleep 1
      done
    fi
  done
  sleep 10
  rm svgs/0.svg
fi

if [ ! -d pngs ]
then
  mkdir -p pngs
  cd svgs
  init=0
  count=0
  width=0
  height=0
  pixel=2048
  switch1=""
  switch2=""
  scale=""
  for i in $( ls -1 *svg ); do
    echo $count :: $i
    name=`echo $i | sed -e "s/.svg//"`
    namevier=`printf "%04d.png" $name`
    if [ $count -eq 0 ]
    then
      rsvg-convert -h $pixel --background-color=white $i -o ../pngs/$namevier
      width=$( pnginfo ../pngs/$namevier | sed -n 's/.*Image Width: //p' | sed -e 's/ .*$//g' )
      height=$( pnginfo ../pngs/$namevier | sed -n 's/.*Image Length: //p' | sed -e 's/ .*$//g' )
      if [ $width -gt $height ]
      then
	      rsvg-convert -w $pixel --background-color=white $i -o ../pngs/$namevier
	      height=$( pnginfo ../pngs/$namevier | sed -n 's/.*Image Length: //p' | sed -e 's/ .*$//g' )
	      switch1="-w"
	      switch2="-h"
	      scale=$height
      else
	      switch1="-h"
	      switch2="-w"
	      scale=$width
      fi
      remainder=`expr $scale % 2`
      if [ $remainder -eq 1 ]
      then
	      scale=`expr $scale + 1`
      fi
    fi
    rsvg-convert $switch1 $pixel $switch2 $scale --background-color=white $i -o ../pngs/$namevier &
    queue $!
    count=`expr $count + 1`
    while [ $NUM -ge 50 ] # MAX PROCESSES
      do
        checkqueue
        sleep 1
    done
  done
  sleep 10
else
  cd svgs
fi

cd ../pngs
ffmpeg -r 3 -i "%04d.png" -vcodec libx264 -vpre libx264-lossless_ultrafast -an -threads 0 $folder.mp4
cd ..
