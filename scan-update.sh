#!/usr/bin/bash

rm "assets/scan/$(ls "assets/scan")"

tree -h app/src >> assets/scan/proect.scan
