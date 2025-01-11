function output = imageFuse(img1, img2)
    output = imfuse(img1, img2, 'blend');
    figure;
    imshow(output);
    title("Blend image by using imfuse");
end

