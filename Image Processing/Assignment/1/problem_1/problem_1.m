function [] = problem_1()

size = [];
loopTime = [];
vectorTime = [];
for i = 2.^(1:10)
    size = [size,i];
    M1 = randi(i,i);
    M2 = randi(i,i);

    disp("Size:" + i);

    tic;
    loopMatrix(M1,M2);
    loopTime = [loopTime, toc];
    disp("Loop:" + toc);

    tic;
    vectorMatrix(M1,M2);
    vectorTime = [vectorTime, toc];
    disp("Vector" + toc + newline);

end

plot(size, loopTime,'DisplayName', 'Nested Loop');
hold on;
plot(size, vectorTime, 'DisplayName', 'Vectorized');
xlabel("Matrix Size (x)");
ylabel("Execution Time(seconds)(y)");
title("Nested Loop vs. Vectorized Matrix Multiplication");
legend('Location', 'Best');
grid on;
end

