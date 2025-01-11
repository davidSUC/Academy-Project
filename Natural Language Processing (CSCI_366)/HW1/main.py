from preprocessing import *
from model_traning import *

train_mapped_text = train_pp("train-Spring2024.txt", "P_train.txt")
train_original_types = get_type("train-Spring2024.txt")

# train_mapped_text = train_pp("train.txt", "P_train.txt")
# train_original_types = get_type("train.txt")
train_mapped_freq = get_freq("P_train.txt")
train_mapped_types = get_type("P_train.txt")

text_mapped_text = test_pp("test.txt", "P_test.txt", train_mapped_types)
test_original_freq = get_freq("test.txt")
test_original_types = get_type("test.txt")
test_mapped_freq = get_freq("P_test.txt")
test_mapped_types = get_type("P_test.txt")

unigram = unigram_model(train_mapped_freq)
bigram = bigram_model(train_mapped_freq, train_mapped_types, train_mapped_text)
bigram_add1 = bigram_model_add1(train_mapped_freq, train_mapped_types, train_mapped_text)

# Report
output = open("report.txt", "w", encoding="utf-8")

# Question_1
output.write("Question 1:\n")
output.write(f"The number of word types in the training corpus is: {len(train_original_types)+1}\n")

# Question_2
output.write("\n\nQuestion 2:\n")
num_tokens_train = sum(train_mapped_freq.values()) - train_mapped_freq['<s>']
output.write(f"Total number word tokens in the training corpus is: {num_tokens_train}\n")

# Question_3
output.write("\n\nQuestion 3:\n")
unseen_types = []
unseen_tokens = 0
for word in test_original_types:
    if word not in train_original_types:
        unseen_types.append(word)
        unseen_tokens += test_original_freq[word]
percent_unseen_types = round((len(unseen_types)) / (len(test_original_types)+1) * 100, 2)
percent_unseen_tokens = round(unseen_tokens / sum(test_mapped_freq.values()) * 100, 2)
output.write(f"Percentage of word types in the test corpus did not occur in training is: {percent_unseen_types}%\n")
output.write(f"Percentage of word tokens in the test corpus did not occur in training is: {percent_unseen_tokens}%\n")

# Question_4
output.write("\n\nQuestion 4:\n")
test_file = open("P_test.txt", "r", encoding="utf-8")
unseen_types = {}
seen_types = {}
num_unseen_types = 0
num_seen_types = 0
num_unseen_tokens = 0
num_seen_tokens = 0

for line in test_file:
    words = line.split()
    words.remove('<s>')
    for i in range(len(words) - 1):
        word1 = words[i]
        word2 = words[i+1]
        if word2 not in bigram[word1]:
            num_unseen_tokens += 1
            if words[i] not in unseen_types:
                unseen_types[words[i]] = {}
            if words[i+1] not in unseen_types[words[i]]:
                unseen_types[words[i]][words[i + 1]] = 1
                num_unseen_types += 1
            else:
                unseen_types[words[i]][words[i + 1]] += 1
        else:
            num_seen_tokens += 1
            if words[i] not in seen_types:
                seen_types[words[i]] = {}
            if words[i+1] not in seen_types[words[i]]:
                seen_types[words[i]][words[i + 1]] = 1
                num_seen_types += 1
            else:
                seen_types[words[i]][words[i + 1]] += 1

total_types = num_unseen_types + num_seen_types
total_tokens = num_unseen_tokens + num_seen_tokens
output.write(f"Percentage of bigrams types not occur in training is:{round(num_unseen_types/total_types*100, 2)}%\n")
output.write(f"Percentage of bigrams tokens not occur in training is:{round(num_unseen_tokens/total_tokens*100, 2)}%\n")
test_file.close()

# Question_5
output.write("\n\nQuestion 5:\n")
text = "I look forward to hearing your reply . "
output.write("Sentence to compute: \"" + text + "\"\n")
text = f'<s> {text.lower()} </s> '
words = text.split()
map_text = ''
for word in words:
    if word in unigram:
        map_text += f'{word} '
    else:
        map_text += '<unk> '
words = map_text.split()

# Compute by Unigram model
unigram_m, unigram_sum_log, result = compute_unigram(unigram, words)
unigram_avg_log = unigram_sum_log / unigram_m
output.write("--------------------------------------------------------------------------------\n")
output.write(result)
output.write(f"\nNumber of tokens is: {unigram_m}\n")
output.write(f"Sum of all log probability is : {unigram_sum_log}\n")
output.write(f"The average log probability is: {unigram_avg_log}\n")
output.write("--------------------------------------------------------------------------------\n")

# Compute in Bigram model
output.write("\n--------------------------------------------------------------------------------\n")
output.write("Bigram Model:\n")
bigram_m, bigram_sum_log, result = compute_bigram(bigram, words)
output.write(result)
if bigram_m != 0:
    bigram_avg_log = bigram_sum_log / bigram_m
    output.write(f"\nNumber of tokens is: {bigram_m}\n")
    output.write(f"Sum of all log probability is : {bigram_sum_log}\n")
    output.write(f"The average log probability is: {bigram_avg_log}\n")
else:
    bigram_avg_log = 0
output.write("--------------------------------------------------------------------------------\n")

# Compute in Bigram model with add one smoothing
output.write("\n--------------------------------------------------------------------------------\n")
output.write('Bigram Model with Add One Smoothing:')
bigram_add1_m, bigram_add1_sum_log, result = compute_bigram_add1(bigram_add1, train_mapped_freq, words)
output.write(result)
bigram_add1_avg_log = bigram_add1_sum_log / bigram_add1_m
output.write(f"\nNumber of tokens is: {bigram_add1_m}\n")
output.write(f"Sum of all log probability is : {bigram_sum_log}\n")
output.write(f"The average log probability is: {bigram_add1_avg_log}\n")
output.write("--------------------------------------------------------------------------------\n")

# Question_6
output.write("\n\nQuestion 6:\n")
unigram_perplexity = 2 ** (-unigram_avg_log)
bigram_perplexity = (2 ** (-bigram_avg_log)) if bigram_avg_log > 0 else 'undefined'
bigram_add1_perplexity = 2 ** (- bigram_add1_avg_log)
output.write(f"The perplexity for the sentence in each model are:\n")
output.write(f"Unigram Model: {unigram_perplexity}\n")
output.write(f"Bigram Model: {bigram_perplexity}\n")
output.write(f"Bigram Model with Add One Smoothing: {bigram_add1_perplexity}\n")

# Question_7
output.write("\n\nQuestion 7:\nThe perplexity of the test corpus in each model are:\n")
words = text_mapped_text.split()

# Unigram
m, log_sum, result = compute_unigram(unigram, words)
unigram_perplexity = 2 ** (-(log_sum/m))

# Bigram
m, log_sum, result = compute_bigram(bigram, words)
bigram_perplexity = (2 ** (-(log_sum/m))) if m > 0 else 'undefined'

# Bigram Add1 Smoothing
m, log_sum, result = compute_bigram_add1(bigram_add1, train_mapped_freq, words)
bigram_add1_perplexity = 2 ** (-(log_sum/m))

output.write(f"Unigram Model: {unigram_perplexity}\n")
output.write(f"Bigram Model: {bigram_perplexity}\n")
output.write(f"Bigram Model with Add One Smoothing: {bigram_add1_perplexity}\n")

output.close()
