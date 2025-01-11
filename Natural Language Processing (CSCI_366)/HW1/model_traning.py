import math


def unigram_model(freq):
    unigram = dict(freq)
    total_count = sum(unigram.values())
    for word, count in unigram.items():
        unigram[word] = count / total_count
    return unigram


def bigram_model(freq, types, text):
    bigram = {}
    words = text.split()

    # for word in types:
    #     bigram[word] = {word_given: 0 for word_given in types}
    # for i in range(len(words) - 1):
    #     bigram[words[i]][words[i + 1]] += 1
    # for word1 in bigram:
    #     for word2 in bigram[word1]:
    #         bigram[word1][word2] /= freq[word1]
    for i in range(len(words) - 1):
        word = words[i]
        word2 = words[i + 1]
        if word not in bigram:
            bigram[word] = {}
        elif word2 not in bigram[word]:
            bigram[word][word2] = 1
        else:
            bigram[word][word2] += 1
    for word1 in bigram:
        for word2 in bigram[word1]:
            bigram[word1][word2] /= freq[word1]

    return bigram


def bigram_model_add1(freq, types, text):
    bigram = {}
    num_types = len(types)
    words = text.split()

    # for word in types:
    #     bigram[word] = {word_given: 1 for word_given in types}
    # for i in range(len(words) - 1):
    #     bigram[words[i]][words[i + 1]] += 1
    # for word1 in bigram:
    #     for word2 in bigram[word1]:
    #         bigram[word1][word2] /= (freq[word1] + num_types)

    for i in range(len(words) - 1):
        word = words[i]
        word2 = words[i + 1]
        if word not in bigram:
            bigram[word] = {}
        elif word2 not in bigram[word]:
            bigram[word][word2] = 1
        else:
            bigram[word][word2] += 1
    for word1 in bigram:
        for word2 in bigram[word1]:
            bigram[word1][word2] /= (freq[word1] + num_types)

    return bigram


def compute_unigram(model, text):
    result = "Unigram Model:\nAll the parameter's probability:\n"
    for word in text:
        result += f'{word}: {model[word]}\n'

    sum_log = 0
    result += "\nThe log base 2 of all the parameter's probability:"
    for word in text:
        this_log = math.log2(model[word])
        result += f'{word}: {this_log}\n'
        sum_log += this_log

    M = len(text)

    return M, sum_log, result


def compute_bigram(model, text):
    result = "All the parameter's probability:\n"
    has_zero_prop = False
    zero_prop_tokens = []
    sum_log = 0
    M = 0

    for i in range(len(text) - 1):
        word1 = text[i]
        word2 = text[i + 1]
        if word2 not in model[word1]:
            zero_prop_tokens.append(f"{text[i]} {text[i + 1]}")
            has_zero_prop = True
            result += f"{word1} {word2}: {0}\n"
        else:
            result += f"{word1} {word2}: {model[word1][word2]}\n"

    if has_zero_prop:
        result += f"\nThe log probability can not compute due to below parameter have 0 probability:\n"
        for token in zero_prop_tokens:
            result += f'{token}\n'
        M = 0
    else:
        result += "\nThe log base 2 of all the parameter's probability:"
        for i in range(len(text) - 1):
            this_log = math.log2(model[text[i]][text[i + 1]])
            result += f"{text[i]} {text[i + 1]}: {this_log}\n"
            sum_log += this_log
            M += 1
    return M, sum_log, result


def compute_bigram_add1(model, freq, text):
    result = "All the parameter's probability:\n"
    num_types = len(model)
    sum_log = 0
    M = 0

    for i in range(len(text) - 1):
        word1 = text[i]
        word2 = text[i + 1]
        if word2 not in model[word1]:
            model[word1][word2] = 1 / (freq[word1] + num_types)
            result += f"{word1} {word2}: {model[word1][word2]}\n"
        else:
            result += f"{word1} {word2}: {model[word1][word2]}\n"

    result += "\nThe log base 2 of all the parameter's probability:"
    for i in range(len(text) - 1):
        this_log = math.log2(model[text[i]][text[i + 1]])
        result += f"{text[i]} {text[i + 1]}: {this_log}\n"
        sum_log += this_log
        M += 1
    return M, sum_log, result
