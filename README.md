# Pokedex
Pokedex using Aritificial Intelligence

Android app that identifies and detects Pokemon in the provided Image using Tensorflow and Firebase MLKit.

The app is going to mimic the famous Pokédex from the all time classic Pokémon series, and it’s going to identify various Pokémons in a given image using a custom image classification model.


TensorFlow Lite is TensorFlow’s lightweight solution for mobile and embedded devices. It enables on-device machine learning inference with low latency and a small binary size. Furthermore, it also uses the Neural Net API available in newer Android APIs to speed up the computation process.

TensorFlow Lite has a huge collection of pre-tested models available, which you can load and use in your Android app. It also allows you to run a custom model, but on the condition that it should be compatible with TFLite. You can read up more on running a custom model over here.

Firebase’s ML Kit can run all the models compatible with TensorFlow Lite on your device and, if needed, you can also host the models on Firebase instead of bundling them in your app (more on this later).
