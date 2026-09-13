"""
Inspect colors and coordinates of test_ip_tarsier_isolated.png
"""
from PIL import Image
import numpy as np

img = Image.open("/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec/test_ip_tarsier_isolated.png")
w, h = img.size
print(f"Size: {w}x{h}")

# Sample body color near center-left
body_color = img.getpixel((int(w * 0.25), int(h * 0.65)))
belly_color = img.getpixel((int(w * 0.50), int(h * 0.70)))
outline_color = img.getpixel((int(w * 0.10), int(h * 0.50)))
head_color = img.getpixel((int(w * 0.50), int(h * 0.20)))

print(f"Body: {body_color}")
print(f"Belly: {belly_color}")
print(f"Outline: {outline_color}")
print(f"Head: {head_color}")
