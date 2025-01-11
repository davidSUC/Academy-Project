function output = matchHist(input1,input2)
    img = imread(input1);
    target = imread(input2);
    output = zeros(size(img), class(img));

    imgR = img(:,:,1);
    imgG = img(:,:,2);
    imgB = img(:,:,3);

    targetR = imhist(target(:,:,1));
    targetG = imhist(target(:,:,2));
    targetB = imhist(target(:,:,3));

    matchR = histeq(imgR, targetR);
    matchG = histeq(imgG, targetG);
    matchB = histeq(imgB, targetB);

    output(:,:,1) = matchR;
    output(:,:,2) = matchG;
    output(:,:,3) = matchB;

end

