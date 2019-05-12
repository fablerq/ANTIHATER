f=open("data.txt", "r")
contents =f.read()
print(contents)
print("тамтам")

z = open("data2.txt","w+")
for i in range(10):
     z.write("hey hey2 \n")
z.close()
print("end")