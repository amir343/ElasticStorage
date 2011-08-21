load cpuLoadController.dump;
load cpuLoadWithoutController.dump;
m1 = size(cpuLoadController, 1);
m2 = size(cpuLoadWithoutController, 1);
xMin = min(m1, m2);
sampling = 25;

X = [0: sampling: sampling*(xMin-1)];
p1 = plot(X, cpuLoadController(1:xMin, 1), '-mo');
set(p1,'Color','red','LineWidth',1);
hold all;
p2 = plot(X, cpuLoadWithoutController(1:xMin, 1), '-.r*');
set(p2,'Color','blue','LineWidth',1);
set(gca, ...
  'XMinorTick'  , 'on'      , ...
  'YMinorTick'  , 'on'      , ...
  'YGrid'       , 'on'      , ...
  'XGrid'       , 'on'      , ...
  'XColor'      , [.3 .3 .3], ...
  'YColor'      , [.3 .3 .3], ...
  'LineWidth'   , 1         );
title('Average CPU Load')
xlabel('Time (s)')
ylabel('Average CPU Load (%)')
legend('w/ controller', 'w/o controller');