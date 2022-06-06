import matplotlib.pyplot as plt
import os
from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model
from flask import send_file
from test import readb64
os.environ['CUDA_VISIBLE_DEVICES'] = '-1'

import base64
import cv2
import  numpy as np
import matplotlib
from PIL import Image

inception_chest = load_model('inceptionv3_chest (3).h5')
print(type(inception_chest))
app = Flask(__name__)



#turn base64 to image



@app.route('/sendbase64',methods=["POST"])
def getbase64():
    img64=request.json['pic']
    print(img64)
    img = readb64(img64)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = cv2.resize(img, (224, 224))
    img = np.array(img) / 255
    img = np.expand_dims(img, axis=0)

    inception_pred = inception_chest.predict(img)
    probability = inception_pred[0]
    #####################

    print("Inception Predictions:")
    if probability[0] > 0.5:
        inception_chest_pred = str('%.2f' % (probability[0] * 100) + '% COVID')
    else:
        inception_chest_pred = str('%.2f' % ((1 - probability[0]) * 100) + '% NonCOVID')
    print(inception_chest_pred)
    ############
    data={
        "percentage":inception_chest_pred
    }
    return jsonify(data)


@app.route('/upload',methods=["POST"])
def upload_file():
    if 'pic' not in request.files:
        resp = jsonify({'message':'No file part in the request'})
        resp.status_code=400
        return resp
    pic=request.files['pic']
    #
    npimg = np.fromfile(pic, np.uint8)
    img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    #
    # convert numpy array to image
    #img = Image.open(request.files['file'])
    img=cv2.cvtColor(img,cv2.COLOR_BGR2RGB)
    img = cv2.resize(img, (224, 224))
    img = np.array(img)/255
    img = np.expand_dims(img, axis=0)

    inception_pred  =inception_chest.predict(img)
    probability = inception_pred[0]
    #####################

    print("Inception Predictions:")
    if probability[0] > 0.5:
        inception_chest_pred = str('%.2f' % (probability[0] * 100) + '% COVID')
    else:
        inception_chest_pred = str('%.2f' % ((1 - probability[0]) * 100) + '% NonCOVID')
    print(inception_chest_pred)
    ############
    return str(inception_chest_pred)

if __name__ == '__main__':
    app.run()
