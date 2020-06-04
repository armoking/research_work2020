import random
import numpy

from keras.utils import to_categorical

from keras.layers import Dense, Conv2D, Flatten, MaxPooling2D
from keras.models import Sequential

import matplotlib.pyplot as plt

f = open('../../images/learn_data.txt')
print('data has been read')

train_present = 0.80
test_present = 0.20
x_train = []
x_test = []
y_train = []
y_test = []

size = list(map(int, f.readline().split()))
index = 0
itr = 0


def read_line():
    res = []
    while len(res) == 0 or len(res[0]) == 0:
        res = f.readline()
    return list(map(int, res.split()))


for letter in range(len(size)):
    train_n = int(size[itr] * train_present)
    test_n = size[itr] - train_n

    data = [read_line() for _ in range(size[itr] * 32)]
    indexes = [i for i in range(size[itr])]
    random.shuffle(indexes)

    for i in range(train_n):
        x_train.append([data[indexes[i] * 32 + x] for x in range(32)])
        y_train.append(letter)

    for i in range(test_n):
        x_test.append([data[indexes[i + train_n] * 32 + x] for x in range(32)])
        y_test.append(letter)

    index += size[itr]
    itr += 1
    print(letter)

print('test data has been built')
x_train = numpy.array(x_train).reshape((len(x_train), 32, 32, 1))
x_test = numpy.array(x_test).reshape((len(x_test), 32, 32, 1))

y_train = to_categorical(y_train)
y_test = to_categorical(y_test)

model = Sequential()
model.add(Conv2D(64, kernel_size=3, activation='relu', input_shape=(32, 32, 1)))
model.add(MaxPooling2D())
model.add(Conv2D(128, kernel_size=4, activation='relu'))
model.add(MaxPooling2D())
model.add(Flatten())
model.add(Dense(len(size), activation='softmax'))
model.compile(optimizer='Adadelta', loss='categorical_crossentropy', metrics=['accuracy'])
history = model.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=10)

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

model.save('fourth_model_same_as_second_but_42_classes_and_good_data.h5')
