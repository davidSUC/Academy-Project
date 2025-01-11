def train_pp(read_file, write_file):
    read = open(read_file, "r", encoding="utf-8")
    pad_file = open(write_file, "w", encoding="utf-8")
    original_freq = get_freq(read_file)
    text_mapped = ""

    for line in read:
        text = line.split()
        text_mapped += "<s> "
        for word in text:
            if original_freq[word.lower()] == 1:
                text_mapped += "<unk> "
            else:
                text_mapped += word.lower() + " "
        text_mapped += "</s>\n"

    pad_file.write(text_mapped)
    read.close()
    pad_file.close()
    return text_mapped


def test_pp(read_file, write_file, train_data):
    read = open(read_file, "r", encoding="utf-8")
    pad_file = open(write_file, "w", encoding="utf-8")
    text_mapped = ""

    for line in read:
        text = line.split()
        text_mapped += "<s> "
        for word in text:
            if word not in train_data:
                text_mapped += "<unk> "
            else:
                text_mapped += word.lower() + " "
        text_mapped += "</s>\n"

    pad_file.write(text_mapped)
    read.close()
    pad_file.close()
    return text_mapped


def get_freq(read_file):
    read = open(read_file, "r", encoding="utf-8")
    count = {}
    for line in read:
        text = line.split()
        for word in text:
            if word.lower() in count:
                count[word.lower()] += 1
            else:
                count[word.lower()] = 1
    read.close()
    return count


def get_type(read_file):
    read = open(read_file, "r", encoding="utf-8")
    types = []
    for line in read:
        text = line.split()
        for word in text:
            if word.lower() not in types:
                types.append(word.lower())
    read.close()
    return types
