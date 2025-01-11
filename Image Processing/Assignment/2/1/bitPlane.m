function output = bitPlane(inputImg, varargin)

    argNums = nargin-1;
    
    img = imread(inputImg);
    sizeImg = size(img);
    
    if (argNums <= 1)        
        output = img;
    else
        output = zeros(sizeImg);
        for i=1:argNums
        output = bitset(output, varargin{i}, bitget(img,varargin{i}));
        end
        output = uint8(output);
    end

    if(argNums == 1)
        output = bitset(output, varargin{1}, 0);
    end

end


