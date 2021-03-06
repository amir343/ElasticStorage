load output_bw_state.dump;
m1 = size(output_bw_state, 1);
sampling = 25;

X = [0: sampling: sampling*(m1-1)];
p1 = plot(X, output_bw_state(1:m1, 1));
set(p1,'Color','blue','LineWidth',1);
set(gca, ...
  'XMinorTick'  , 'on'      , ...
  'YMinorTick'  , 'on'      , ...
  'YGrid'       , 'on'      , ...
  'XGrid'       , 'on'      , ...
  'XColor'      , [.3 .3 .3], ...
  'YColor'      , [.3 .3 .3], ...
  'LineWidth'   , 1         );
hTitle = title('System Identification - Average Bandwidth per download');
hXLabel = xlabel('Time (s)');
hYLabel = ylabel('Average Bandwidth (B/s)');
set([hXLabel, hYLabel]  , ...
    'FontSize'   , 10          );
set( hTitle                    , ...
    'FontSize'   , 10          , ...
    'FontWeight' , 'bold'      );