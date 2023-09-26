{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}
{-# HLINT ignore "Use camelCase" #-}
import Control.Concurrent
import Control.Monad
import Data.Time
import Control.Monad.RWS (MonadState(put))

pegar_carro_pcd :: Int -> Int -> UTCTime -> IO ()
pegar_carro_pcd carro vaga start = do
        tempo1 <- fmap realToFrac (diffUTCTime <$> getCurrentTime <*> pure start)
        putStrLn $ "O carro " ++ show carro ++ " estacionou na vaga " ++ show vaga ++ " no tempo " ++ show tempo1
        threadDelay 1500000
        tempo <- fmap realToFrac (diffUTCTime <$> getCurrentTime <*> pure start)
        putStrLn $ "O carro " ++ show carro ++ " saiu da vaga " ++ show vaga ++ " no tempo " ++ show tempo


pegar_carro :: Int -> Int -> UTCTime -> IO ()
pegar_carro carro vaga start = do
        tempo1 <- fmap realToFrac (diffUTCTime <$> getCurrentTime <*> pure start)
        putStrLn $ "O carro " ++ show carro ++ " estacionou na vaga " ++ show vaga ++ " no tempo " ++ show tempo1
        threadDelay 1000000
        tempo <- fmap realToFrac (diffUTCTime <$> getCurrentTime <*> pure start)
        putStrLn $ "O carro " ++ show carro ++ " saiu da vaga " ++ show vaga ++ " no tempo " ++ show tempo

tz :: IO ()
tz  =  do
    n <- readLn
    k <- readLn
    starttime <- getCurrentTime

    -- Crie uma MVar que conterá a lista de inteiros
    listacarros <- newMVar [1..n]
    listavagas <- newMVar [1..k]

    forM_ [1..k] $ \vaga -> do
        -- define o numero da vaga que vai ser essa thread
        forkIO $ do
            vagas <- takeMVar listavagas
            let vaga = head vagas
                novalistavagas = tail vagas
            putMVar listavagas novalistavagas
            
            
            -- pega um carro 
            if vaga <= k `div` 10
                then do
                    carros <- takeMVar listacarros
                    let carro = head carros
                        novalistacarros = tail carros
                    putMVar listacarros novalistacarros
                    if carro <= n `div` 5
                        then do
                        pegar_carro carro vaga starttime
                        else putStrLn ""
                else do
                    carros <- takeMVar listacarros
                    let carro = last carros
                        novalistacarros = init carros
                    putMVar listacarros novalistacarros
                    pegar_carro carro vaga starttime


        putStrLn "FIM DA MAIN"