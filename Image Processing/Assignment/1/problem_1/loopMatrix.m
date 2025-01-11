function output = loopMatrix(matrix1, matrix2)
    s = size(matrix1);
    r = s(1);
    c = s(2);

    output = zeros(r,c);

    for i=1:r
        for j=1:c
            output(i,j) = matrix1(i,:) * matrix2(:,j);
        end
    end
end

