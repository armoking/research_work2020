from keras.models import load_model
import numpy as np

filename = 'C:\\Users\\User\\Desktop\\test_result.txt'
cnn = load_model('main_cnn.h5')
alphabet = list('0123456789аАбБвВгГдДежзийклмнопПрРстТуУфхцчшщъыьэюя')
answer = ''

inp = list(map(int, open(filename).read().split()))

index = 0


def read_int():
    global index
    result = inp[index]
    index += 1
    return result


letters_count = read_int()

for _ in range(letters_count):
    n, m = read_int(), read_int()
    cnt = [[0 for _ in range(32)] for _ in range(32)]
    arr = [[0 for _ in range(32)] for _ in range(32)]
    for x in range(n):
        for y in range(m):
            val = read_int()
            x_index = int(x * 32 / n)
            y_index = int(y * 32 / m)
            cnt[x_index][y_index] += 1
            arr[x_index][y_index] += val

    for x in range(32):
        for y in range(32):
            arr[x][y] //= max(1, cnt[x][y])

    prediction = cnn.predict(np.array(arr).reshape((1, 32, 32, 1))).reshape(51)
    max_probability = 0
    current_result = '?'
    for i in range(51):
        if prediction[i] > max_probability:
            max_probability = prediction[i]
            current_result = alphabet[i]
    answer += current_result

print(answer)
