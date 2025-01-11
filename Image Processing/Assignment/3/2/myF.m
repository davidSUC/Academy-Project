function [output, avgArea, thrList] = myF(input)
    img = input;
    
    % preprocess
    smoothImg = adapthisteq(img);

    % computed threshold
    thr = multithresh(smoothImg);

    % Apply threshold
    binaryImg = smoothImg > thr;

    % Erase left over small object/noise
    output = bwareaopen(binaryImg, 100);

    % Component imformation
    cInfo = bwconncomp(output);
    numC = cInfo.NumObjects;
    area = histcounts(output);
    avgArea = area(2)\numC;
    
    % Further enhancement process
    tmpThr = thr;
    maxThr = ceil(thr*1.3); % manually decided the range of possible threshold value
    
    testNumC = numC;
    while(testNumC >= numC) % find min threshold value.
        minThr = tmpThr;
        tmpThr = tmpThr-1;
        testImg = bwareaopen(smoothImg > tmpThr, 100);
        testNumC = bwconncomp(testImg).NumObjects;
    end

    thrList = [minThr];
    tmpThr = thr+1;
    maxC = numC;
    while(tmpThr <= maxThr) % find the all possible threshold value in the predict range, that preserving all components in good shape.
        testImg = bwareaopen(smoothImg > tmpThr, 100);
        testNumC = bwconncomp(testImg).NumObjects;
        if(testNumC > maxC)
            thrList(end+1) = tmpThr;
            maxC = testNumC;
        end
        tmpThr = tmpThr + 1;
    end

    output = bwlabel(output);
    output = label2rgb(output);

end

