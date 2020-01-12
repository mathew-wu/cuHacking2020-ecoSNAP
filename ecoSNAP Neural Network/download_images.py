# import the necessary packages
from imutils import paths
import argparse
import requests
import cv2
import os
from pathlib import Path

# grab the list of URLs from the input file, then initialize the
# total number of images downloaded thus far
rows = open("urls.txt").read().strip().split("\n")
total = 0

# loop the URLs
for url in rows:
    try:
		# try to download the image
        total += 1
        r = requests.get(url, timeout=60)
 
		# save the image to disk
        p = Path("cans{}.jpg".format(total))
        f = open(p, "wb")
        f.write(r.content)
        f.close()
 
		# update the counter
        print("[INFO] downloaded: {}".format(p))
        
 
	# handle if any exceptions are thrown during the download process
    except:
        print("[INFO] error downloading {}...skipping".format(p))
# loop over the image paths we just downloaded
for imagePath in paths.list_images(args["output"]):
	# initialize if the image should be deleted or not
	delete = False
 
	# try to load the image
	try:
		image = cv2.imread(imagePath)
 
		# if the image is `None` then we could not properly load it
		# from disk, so delete it
		if image is None:
			delete = True
 
	# if OpenCV cannot load the image then the image is likely
	# corrupt so we should delete it
	except:
		print("Except")
		delete = True
 
	# check to see if the image should be deleted
	if delete:
		print("[INFO] deleting {}".format(imagePath))
		os.remove(imagePath)