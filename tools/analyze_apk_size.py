import zipfile
import os

apk_path = "playit-debug.apk"
zf = zipfile.ZipFile(apk_path, "r")
sizes = {}
compressed_sizes = {}
file_details = []

for info in zf.infolist():
    parts = info.filename.split("/")
    cat = parts[0]
    if cat == "assets" and len(parts) > 1:
        if parts[1] in ("images", "audio"):
            cat = "assets/" + parts[1]
            if len(parts) > 2:
                cat += "/" + parts[2]
        else:
            cat = "assets/" + parts[1]
    elif cat == "lib" and len(parts) > 1:
        cat = "lib/" + parts[1]

    sizes[cat] = sizes.get(cat, 0) + info.file_size
    compressed_sizes[cat] = compressed_sizes.get(cat, 0) + info.compress_size
    file_details.append((info.filename, info.file_size, info.compress_size))

print("=" * 72)
print(f"Total uncompressed: {sum(sizes.values()) / (1024*1024):.2f} MB")
print(f"APK physical size:  {os.path.getsize(apk_path) / (1024*1024):.2f} MB")
print("=" * 72)
print(f"{'Category':<38} {'Uncompressed MB':>15} {'Compressed MB':>15}")
print("-" * 72)
for c, sz in sorted(compressed_sizes.items(), key=lambda x: -x[1]):
    print(f"{c:<38} {sizes[c] / (1024*1024):>14.2f}M {sz / (1024*1024):>14.2f}M")

print("\n" + "=" * 72)
print("Top 25 Largest Files in APK:")
print("-" * 72)
file_details.sort(key=lambda x: -x[2])
for fname, uncomp, comp in file_details[:25]:
    print(f"{comp / (1024*1024):6.2f}M (uncomp: {uncomp / (1024*1024):6.2f}M)  {fname}")
print("=" * 72)
