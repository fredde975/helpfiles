#!/bin/bash
# Batch convert videos with HandBrake CLI
# By Ralph Crisostomo - 2016.04.17
#
# Usage :
#   'sudo ./handbrake.sh /source /destination'
#
# Reference :
#   https://forum.handbrake.fr/viewtopic.php?f=6&t=19426
#   https://gist.github.com/czj/1263872
#   https://trac.handbrake.fr/wiki/BuiltInPresets#universal
#

SOURCE=$1

# Universal
# ref: https://trac.handbrake.fr/wiki/BuiltInPresets#universal
#


while IFS= read -d '' -r ITEM
do

  echo "processing $ITEM"

  FILE=${ITEM##*/}
  
  # fredrik: will only create .mp4 file
  EXT="mp4"
  
  # fredrik: use the input directory as the output directory for the created .mp4 file
  OUTPUT="$(dirname $ITEM)/${FILE%.*}.$EXT"

  echo "The output file will be: $OUTPUT"

  echo "" | HandBrakeCLI --preset-import-file fast_1080p_30_to_mp4.json -i "$ITEM" -o "$OUTPUT"

#done< <(find "$SOURCE" \( -iname '*.mp4' -or -iname '*.avi'  -or -iname '*.mkv' -or -iname '*.mts' \) -print0)

# fredrik:  for being more specific in files to handle
# find . -not -iname '*.jpg' -not -iname '*.bmp' -not -iname '*.zip' -not  -type d
done< <(find "$SOURCE" \( -iname '*.avi'  -or -iname '*.mkv' -or -iname '*.mts' \) -print0)

