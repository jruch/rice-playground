ALTER TABLE KOM_ORGANIZATION_T
ADD CONSTRAINT KOM_ORGANIZATION_UK1 UNIQUE
(
NAME
) ENABLE
/
ALTER TABLE KOM_ORGANIZATION_T
ADD CONSTRAINT KOM_ORGANIZATION_FK1 FOREIGN KEY
(
CATEGORY_ID
)
REFERENCES KOM_ORGANIZATION_CATEGORY_T
(
ID
) ENABLE
/