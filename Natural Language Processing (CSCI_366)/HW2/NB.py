import ast
import math


def trainNB(bag_ow, num_in_label, vocab):
    pp = {}
    for label in bag_ow:
        pp[label] = {}
        for word in vocab:
            count = 0
            if word in bag_ow[label]:
                count = bag_ow[label][word]
            pp[label][word] = math.log2((count + 1) / (num_in_label[label] + len(vocab)))
    return pp


def print_pp(pp, pp_prior, vocab, model):
    model.write("Prior probability:\n")
    for label in pp_prior:
        model.write(f"P({label}) = {pp_prior[label]}\n")

    model.write("\nProbability of each word:\n")
    for label in pp:
        for word in vocab:
            model.write(f"P({word} | {label}) = {pp[label][word]}\n")
    return


def arg_max(prop):
    max_key = max(prop, key=prop.get)
    return max_key

# training = open(sys.argv[1], "r", encoding='utf-8')
# test = open(sys.argv[2], "r", encoding='utf-8')
# model = open(sys.argv[3], "w", encoding='utf-8')
# output = open(sys.argv[4], "w", encoding='utf-8')


flag = 1
print("Input 1 or 2 to choose which data set to be test:")
print("1) Movie review corpus")
print("2) Small corpus")
print("Input 0 to exit program")
while flag:
    user_input = input()
    if user_input == '1':
        training = open("movie-review-BOWtrain.NB", "r", encoding='utf-8')
        test = open("movie-review-BOWtest.NB", "r", encoding='utf-8')
        model = open("movie-review.NB", "w", encoding='utf-8')
        output = open("output.txt", "w", encoding='utf-8')
        flag = 0
    elif user_input == '2':
        training = open("movie-review-BOWsmall.NB", "r", encoding='utf-8')
        test = open("small-test.txt", "r", encoding='utf-8')
        model = open("movie-review-small.NB", "w", encoding='utf-8')
        output = open("output.txt", "w", encoding='utf-8')
        flag = 0
    elif user_input == '0':
        flag = 0
        exit()
    else:
        print("Input must be 1 or 2 or 0")


v = []
vocab = open("imdb.vocab", "r", encoding='utf-8')
for line in vocab:
    v.append(line.strip())

bagOW = {}
doc_num_in_label = {}
for line in training.readlines():
    vector = ast.literal_eval(line)
    for key in vector:
        if key not in bagOW:
            bagOW[key] = {}
            doc_num_in_label[key] = 0
        for word, count in vector[key].items():
            bagOW[key][word] = bagOW[key].get(word, 0) + count
        doc_num_in_label[key] += 1
word_num_in_label = {}
for key in bagOW:
    word_num_in_label[key] = sum(bagOW[key].values())

pp = trainNB(bagOW, word_num_in_label, v)
pp_prior = {}
for label in doc_num_in_label:
    pp_prior[label] = math.log2(doc_num_in_label[label] / sum(doc_num_in_label.values()))
print_pp(pp, pp_prior, v, model)

# test
pp_in_label = {}
c = 0
correct = 0
n_correct = 0
true_label = ''
s = "Doc#\t|\tPredicted class\t|\tActual class\n\t--------------------------------------\t\n"
stop_words = {'a', 'about', 'above', 'after', 'again', 'against', 'all', 'am', 'an', 'and', 'any', 'are', 'as', 'at',
              'be', 'because', 'been', 'before', 'being', 'below', 'between', 'both', 'but', 'by', 'can', 'cannot',
              'could', 'did', 'do', 'does', 'doing', 'down', 'during', 'each', 'few', 'for', 'from', 'further', 'had',
              'has', 'have', 'having', 'he', 'her', 'here', 'hers', 'herself', 'him', 'himself', 'his', 'how', 'i',
              'if', 'in', 'into', 'is', 'it', 'its', 'itself', 'me', 'more', 'most', 'my', 'myself', 'no', 'nor', 'not',
              'of', 'off', 'on', 'once', 'only', 'or', 'other', 'ought', 'our', 'ours', 'ourselves', 'out', 'over',
              'own', 'same', 'she', 'should', 'so', 'some', 'such', 'than', 'that', 'the', 'their', 'theirs', 'them',
              'themselves', 'then', 'there', 'these', 'they', 'this', 'those', 'thus', 'to', 'too', 'under',
              'underneath', 'up', 'upon', 'us', 'use', 'used', 'uses', 'using', 'was', 'we', 'were'}
for line in test.readlines():
    for label in pp_prior:
        pp_in_label[label] = pp_prior[label]
    vector = ast.literal_eval(line)
    for key in vector:
        true_label = key
        for word, count in vector[key].items():
            for label in pp_prior:
                if word in stop_words:
                    continue
                pp_in_label[label] += pp[label][word]
    maxArg = arg_max(pp_in_label)
    if maxArg == true_label:
        correct += 1
    else:
        n_correct += 1
    c += 1
    # s += f"{c}\t\t|\t\t{maxArg}\t\t\t|\t{true_label}\n"
    s += f"{c}\t|\t{maxArg}\t\t|\t{true_label}\n"

s += f"\nTotal number of documents: {c}\nPrediction: True={correct}, False={n_correct}\nAccuracy: {round((correct / c) * 100, 2)}%"
output.write(s)

training.close()
test.close()
model.close()
output.close()
