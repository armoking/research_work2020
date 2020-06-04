import random
import numpy

f = open('../../data_set.txt')

train_present = 0.80
test_present = 0.20
x_train = []
x_test = []
y_train = []
y_test = []

count_of_classes = 51

N = int(f.readline()) // 2
size = [966, 966, 966, 966, 966, 966, 966, 966, 966, 966, 5313, 4347, 5313, 4347, 5313, 4347, 5313, 4347, 5313, 4347,
        9660, 9660, 9660, 9660, 9660, 9660, 9660, 9660, 9660, 9660, 5313, 4347, 5313, 4347, 9660, 5313, 4347, 5313,
        4347, 9660, 9660, 9660, 9660, 9660, 9660, 5313, 5313, 5313, 9660, 9660, 9660]


def read_line():
    res = []
    while len(res) == 0 or len(res[0]) == 0:
        res = f.readline()
    return list(map(int, res.split()))


for letter in range(51):
    data = []

    for _ in range(size[letter]):
        f.readline()
        for __ in range(32):
            data.append(read_line())

    while len(data) // 16 < 9660:
        data = data + data

    train_n = int(len(data) // 32 * train_present)
    test_n = len(data) // 32 - train_n

    indexes = [i for i in range(len(data) // 32)]
    random.shuffle(indexes)

    for i in range(train_n):
        x_train.append([data[indexes[i] * 32 + x] for x in range(32)])
        y_train.append(letter)

    for i in range(test_n):
        x_test.append([data[indexes[i + train_n] * 32 + x] for x in range(32)])
        y_test.append(letter)

print('test data has been built')
x_train = numpy.array(x_train).reshape((len(x_train), 32, 32, 1))
x_test = numpy.array(x_test).reshape((len(x_test), 32, 32, 1))

from keras.utils import to_categorical

from keras.layers import Dense, Conv2D, Flatten, MaxPooling2D
from keras.models import Sequential

import matplotlib.pyplot as plt

y_train = to_categorical(y_train)
y_test = to_categorical(y_test)

model = Sequential()
model.add(Conv2D(256, kernel_size=3, activation='relu', input_shape=(32, 32, 1)))
model.add(Conv2D(256, kernel_size=3, activation='relu', input_shape=(32, 32, 1)))
model.add(Conv2D(256, kernel_size=3, activation='relu', input_shape=(32, 32, 1)))
model.add(MaxPooling2D())
model.add(Flatten())
model.add(Dense(256, activation='relu'))
model.add(Dense(count_of_classes, activation='softmax'))
model.compile(optimizer='Adadelta', loss='categorical_crossentropy', metrics=['accuracy'])
history = model.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=5)

plt.plot(history.history['accuracy'])
plt.plot(history.history['val_accuracy'])
plt.title('Model accuracy')
plt.ylabel('Accuracy')
plt.xlabel('Epoch')
plt.legend(['Train', 'Test'], loc='upper left')
plt.show()

plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('Model loss')
plt.ylabel('Loss')
plt.xlabel('Epoch')
plt.legend(['Train', 'Test'], loc='upper left')
plt.show()

model.save('letter_detecting_v7.h5')
