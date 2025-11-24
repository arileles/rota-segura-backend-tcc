ALTER TABLE media DROP COLUMN image_data;
ALTER TABLE media ADD COLUMN image_data oid;
alter TABLE media DROP COLUMN url;