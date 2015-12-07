#!/bin/bash

# zweite Spalte ausgeben - Benutzernamen auflisten
awk -F ',' '{print $2}' user.csv
