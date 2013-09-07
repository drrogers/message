rm -rf grogers.message.tar
rm -rf grogers.message
cp -R python grogers.message
rm grogers.message/*.pyc
tar cvf grogers.message.tar grogers.message
mv grogers.message.tar WebContent/static/
cp python/MessageDemo.docx WebContent/static
