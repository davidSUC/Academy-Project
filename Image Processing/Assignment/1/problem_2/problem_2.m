image1 = imread('Everest_expedition.jpg');
image2 = imread('Everest_kalapatthar.jpg');

[padImg1, padImg2] = imagePad(image1, image2);
fuseImage = imageFuse(image1, image2);