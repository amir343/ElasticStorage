load ../dumps/rt.dump;
y = rt(:,1);
tp = rt(:,2);
cpu = rt(:,3);
bw = rt(:,4);
nn = rt(:,5);

X = [tp cpu bw nn];
b = regress(y,X);

scatter3(tp,nn,y,'filled')
hold on
xlabel('Throughput')
ylabel('Number of nodes')
zlabel('Average CPU')
view(50,10)