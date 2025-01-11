function [transformed_image, theta] = transform(fixed_pts, moving_pts, moving_image)

    theta = findTheta(fixed_pts, moving_pts);
    [rows, cols] = size(moving_image);
    transformed_image = zeros(size(moving_image));

    for x = 1:cols
        for y = 1:rows
            new_pos = invXform([x, y], theta);
            % mask moving image to fixed image space
            if new_pos(1) >= 1 && new_pos(1) <= cols && new_pos(2) >= 1 && new_pos(2) <= rows
                transformed_image(y, x) = moving_image(floor(new_pos(2)), floor(new_pos(1)));
            else
                transformed_image(y, x) = 128; % Set out boundary gray
            end
        end
    end
end

function theta = findTheta(fixed_pts, moving_pts)
    Xi = zeros(2*size(moving_pts,1), 4);
    f = zeros(2*size(moving_pts,1), 1);
    for i = 1:size(moving_pts,1)
        Xi(2*i-1, :) = [moving_pts(i,1), -moving_pts(i,2), 1, 0];
        Xi(2*i, :) = [moving_pts(i,2), moving_pts(i,1), 0, 1];
        f(2*i-1) = fixed_pts(i,1);
        f(2*i) = fixed_pts(i,2);
    end
    theta = Xi\f;  % inv((Xi'*Xi)) * (Xi'*f)
end

function new_pos = invXform(pos, theta)
    x = pos(1);
    y = pos(2);
    s = theta(1);
    phi = theta(2);
    tx = theta(3);
    ty = theta(4);
    Tx = (cos(phi) * (x-tx) + sin(phi) * (y-ty)) / s;
    Ty = (-sin(phi) * (x-tx) + cos(phi) * (y-ty)) / s;
    new_pos = [Tx, Ty];
end
