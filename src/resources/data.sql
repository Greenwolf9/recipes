INSERT INTO USERS (EMAIL, USER_PASSWORD, USER_ROLE) VALUES ( 'mockingbird@gmail.com','$2a$12$AXjg0Woh2Wn2W4o8QK6qguTL.uqQaAmE6lDVKTkCx./35NYpShloO', '' );

INSERT INTO RECIPES(ID, RECIPE_NAME, CATEGORY, RECIPE_DATE, DESCRIPTION, INGREDIENTS,DIRECTIONS, USER_EMAIL)
VALUES (1,'Fresh Mint Tea /Test', 'beverage /Test',
        current_timestamp,
        'Light, aromatic and refreshing beverage, ... /Test',
        ARRAY ['Boiled water'],
        ARRAY ['Boil water'],
        'mockingbird@gmail.com');
