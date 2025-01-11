function output = laplacian3x3(inputArg1,inputArg2)
    img = inputArg1
    filter = inputArg2

    imgSize = size(img);
    output = zeros(imgSize);

    frame = zeros(imgSize+2);
    frameSize = size(frame);
    frame(2:frameSize(1)-1,2:frameSize(2)-1) = img(:,:);

    for i=2:frameSize(1)-1
        for j=2:frameSize(2)-1
            n = frame(i-1:i+1, j-1:j+1).*filter;
            output(i-1,j-1) = sum(n, "all");
        end
    end
end

