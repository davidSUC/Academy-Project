import sys
import os
import re

# path = sys.argv[1]
# path = "small/"
# path = "reviews/test/"
path = "reviews/train/"

output_name = "movie-review-BOW" + path.replace('/', " ").split()[-1] + ".NB"
output = open(output_name, "w", encoding='utf-8')

v = []
vocab = open("imdb.vocab", "r", encoding='utf-8')
for line in vocab:
    v.append(line.strip())

for label in os.listdir(path):
    folder = os.path.join(path, label)
    for filename in os.listdir(folder):
        file = open(os.path.join(folder, filename), "r", encoding='utf-8')
        text = file.read().lower().strip()
        text = re.sub(r'[^\w\s]', '', text)
        words = text.split()
        freq = {}
        for word in words:
            if word not in v:
                words.remove(word)
            else:
                if word in freq:
                    freq[word] += 1
                else:
                    freq[word] = 1
        data = {label: freq}
        output.write(str(data)+'\n')

output.close()
vocab.close()
