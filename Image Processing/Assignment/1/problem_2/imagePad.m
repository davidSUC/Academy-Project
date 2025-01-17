function [output1, output2] = imagePad(img1, img2)

    max_greyValue = 255;

    size1 = size(img1);
    size2 = size(img2);
    maxSize = [max(size1(1), size2(1)), max(size1(2), size2(2))];

    scale_1 = [maxSize(1)-size1(1), maxSize(2)-size1(2)] / 2;
    scale_2 = [maxSize(1)-size2(1), maxSize(2)-size2(2)] / 2;

    figure;
    padImg_1 = zeros(maxSize(1), maxSize(2), size1(3), class(img1));
    padImg_2 = zeros(maxSize(1), maxSize(2), size2(3), class(img2));
    padImg_1(1+scale_1(1):size1(1)+scale_1(1), 1+scale_1(2):size1(2)+scale_1(2), :) = img1;
    padImg_2(1+scale_2(1):size2(1)+scale_2(1), 1+scale_2(2):size2(2)+scale_2(2), :) = img2;
    output1 = padImg_1 + padImg_2;
    output1 = min(output1, max_greyValue);
    imshow(output1);
    title("Padded in the middle");
    
    figure;
    padImg_1 = zeros(maxSize(1), maxSize(2), size1(3), class(img1));
    padImg_2 = zeros(maxSize(1), maxSize(2), size2(3), class(img2));
    padImg_1(1:size1(1), 1:size1(2), :) = img1;
    padImg_2(1:size2(1), 1:size2(2), :) = img2;
  % immad(img1, img2) == padImg_1 + padImg_2.
    output2 = imadd(padImg_1, padImg_2);
    imshow(output2);
    title("Padded from the top-left corner");
end