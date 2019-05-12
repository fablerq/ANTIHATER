from keras.layers import LSTM
from keras.models import Sequential
from keras.layers import  CuDNNLSTM, Bidirectional, CuDNNGRU
from keras import backend as K
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
import numpy as np
import pandas as pd
from keras.utils import np_utils
from keras.layers import Dense, Dropout, Embedding

lang = "english"

def f1(y_true, y_pred):
  def recall(y_true, y_pred):
    """Recall metric.

    Only computes a batch-wise average of recall.

    Computes the recall, a metric for multi-label classification of
    how many relevant items are selected.
    """
    true_positives = K.sum(K.round(K.clip(y_true * y_pred, 0, 1)))
    possible_positives = K.sum(K.round(K.clip(y_true, 0, 1)))
    recall = true_positives / (possible_positives + K.epsilon())
    return recall

  def precision(y_true, y_pred):
    """Precision metric.

    Only computes a batch-wise average of precision.

    Computes the precision, a metric for multi-label classification of
    how many selected items are relevant.
    """
    true_positives = K.sum(K.round(K.clip(y_true * y_pred, 0, 1)))
    predicted_positives = K.sum(K.round(K.clip(y_pred, 0, 1)))
    precision = true_positives / (predicted_positives + K.epsilon())
    return precision
  precision = precision(y_true, y_pred)
  recall = recall(y_true, y_pred)
  return 2*((precision*recall)/(precision+recall+K.epsilon()))

use_gpu = False

max_features = 2000
embedding_size = 64
max_sentence_len = 100
tokenizer = Tokenizer(num_words=max_features, split=' ')


model = Sequential()
model.add(Embedding(max_features, embedding_size, input_length = max_sentence_len))
if use_gpu:
  model.add(Bidirectional(CuDNNGRU(200, return_sequences=True)))
else:
  model.add(Bidirectional(LSTM(200, return_sequences=True)))
model.add(Dropout(0.25))
if use_gpu:
  model.add(Bidirectional(CuDNNLSTM(200)))
else:
  model.add(Bidirectional(LSTM(200)))
model.add(Dense(3,activation='sigmoid'))
model.compile(loss = 'binary_crossentropy', optimizer='adam',metrics = ['accuracy', f1])
print(model.summary())

from os.path import exists
if exists("model_{}.h5".format(lang)):
  model.load_weights("model_{}.hdf5".format(lang))

def predict(msg):
  msg = tokenizer.texts_to_sequences(msg)
  msg = pad_sequences(msg, maxlen=max_sentence_len, dtype='int32', value=0)
  sentiment = model.predict(msg,batch_size=1,verbose = 2)[0]
  return str(np.argmax(sentiment))

import json
result = dict()
output = open("result.json", "w")
if exists("data.txt"):
  with open("data.txt", "r") as f:
    for line in f:
      result[line] = predict(line)
json.dump(result, output)

